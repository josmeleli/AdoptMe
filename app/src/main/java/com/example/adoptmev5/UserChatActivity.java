package com.example.adoptmev5;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptmev5.adapters.MessagesAdapter;
import com.example.adoptmev5.api.MessagesAPI;
import com.example.adoptmev5.models.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UserChatActivity extends AppCompatActivity {

    private RecyclerView messagesRecyclerView;
    private EditText messageInput;
    private ImageButton btnSend;
    private ImageView btnBack;
    private ProgressBar loadingProgress;
    private TextView chatTitle;
    private TextView chatSubtitle;

    private MessagesAdapter messagesAdapter;
    private List<Message> messagesList;
    private int currentUserId;

    private Handler autoRefreshHandler;
    private Runnable autoRefreshRunnable;
    private static final int REFRESH_INTERVAL = 5000; // 5 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);

        // Obtener userId de SharedPreferences
        SharedPreferences prefs = getSharedPreferences("adoptme_prefs", MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", -1);

        if (currentUserId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupListeners();
        loadMessages();
        setupAutoRefresh();
    }

    private void initViews() {
        messagesRecyclerView = findViewById(R.id.messages_recycler_view);
        messageInput = findViewById(R.id.message_input);
        btnSend = findViewById(R.id.btn_send);
        btnBack = findViewById(R.id.btn_back);
        loadingProgress = findViewById(R.id.loading_progress);
        chatTitle = findViewById(R.id.chat_title);
        chatSubtitle = findViewById(R.id.chat_subtitle);
    }

    private void setupRecyclerView() {
        messagesList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(messagesList, currentUserId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Comenzar desde abajo
        messagesRecyclerView.setLayoutManager(layoutManager);
        messagesRecyclerView.setAdapter(messagesAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> sendMessage());

        // También enviar con Enter
        messageInput.setOnEditorActionListener((v, actionId, event) -> {
            sendMessage();
            return true;
        });
    }

    private void loadMessages() {
        loadingProgress.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                JSONObject response = MessagesAPI.getChatForUser(currentUserId, 100);

                runOnUiThread(() -> {
                    loadingProgress.setVisibility(View.GONE);
                    if (response != null && response.optBoolean("success", false)) {
                        parseMessages(response);
                    } else {
                        String error = response != null ? response.optString("message", "Error al cargar mensajes") : "Error de conexión";
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    loadingProgress.setVisibility(View.GONE);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void parseMessages(JSONObject response) {
        try {
            JSONArray messagesArray = response.getJSONArray("messages");
            List<Message> newMessages = new ArrayList<>();

            for (int i = 0; i < messagesArray.length(); i++) {
                JSONObject msgObj = messagesArray.getJSONObject(i);
                Message message = new Message();
                message.setId(msgObj.optInt("id"));
                message.setSenderId(msgObj.optInt("sender_id"));
                message.setReceiverId(msgObj.optInt("receiver_id"));
                message.setMessage(msgObj.optString("message"));
                message.setIsRead(msgObj.optInt("is_read"));
                message.setCreatedAt(msgObj.optString("created_at"));
                message.setSenderEmail(msgObj.optString("sender_email"));
                message.setSenderName(msgObj.optString("sender_name"));
                message.setSenderRole(msgObj.optString("sender_role"));
                message.setMine(msgObj.optBoolean("is_mine"));
                message.setFromAdmin(msgObj.optBoolean("is_from_admin"));

                newMessages.add(message);
            }

            messagesAdapter.updateMessages(newMessages);
            messagesRecyclerView.scrollToPosition(newMessages.size() - 1);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar mensajes", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendMessage() {
        String messageText = messageInput.getText().toString().trim();
        if (messageText.isEmpty()) {
            Toast.makeText(this, "Escribe un mensaje", Toast.LENGTH_SHORT).show();
            return;
        }

        messageInput.setText("");
        btnSend.setEnabled(false);

        new Thread(() -> {
            try {
                // Usuario envía mensaje (va a todos los admins automáticamente)
                JSONObject response = MessagesAPI.sendMessage(currentUserId, messageText, null);

                runOnUiThread(() -> {
                    btnSend.setEnabled(true);
                    if (response != null && response.optBoolean("success", false)) {
                        // Recargar mensajes
                        loadMessages();
                    } else {
                        String error = response != null ? response.optString("message", "Error al enviar mensaje") : "Error de conexión";
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    btnSend.setEnabled(true);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void setupAutoRefresh() {
        autoRefreshHandler = new Handler(Looper.getMainLooper());
        autoRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadMessagesQuietly();
                autoRefreshHandler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
        autoRefreshHandler.postDelayed(autoRefreshRunnable, REFRESH_INTERVAL);
    }

    private void loadMessagesQuietly() {
        // Cargar mensajes sin mostrar loading
        new Thread(() -> {
            try {
                JSONObject response = MessagesAPI.getChatForUser(currentUserId, 100);
                if (response != null && response.optBoolean("success", false)) {
                    runOnUiThread(() -> parseMessages(response));
                }
            } catch (Exception e) {
                // Ignorar errores en refresh automático
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (autoRefreshHandler != null && autoRefreshRunnable != null) {
            autoRefreshHandler.removeCallbacks(autoRefreshRunnable);
        }
    }
}

