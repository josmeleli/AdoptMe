package com.example.adoptmev5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptmev5.adapters.ChatUsersAdapter;
import com.example.adoptmev5.api.MessagesAPI;
import com.example.adoptmev5.models.ChatUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminUsersListActivity extends AppCompatActivity {

    private RecyclerView usersRecyclerView;
    private ProgressBar loadingProgress;
    private LinearLayout emptyState;
    private ImageView btnBack;

    private ChatUsersAdapter usersAdapter;
    private List<ChatUser> usersList;
    private int currentAdminId;

    private Handler autoRefreshHandler;
    private Runnable autoRefreshRunnable;
    private static final int REFRESH_INTERVAL = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_users_list);

        // Obtener adminId de SharedPreferences
        SharedPreferences prefs = getSharedPreferences("adoptme_prefs", MODE_PRIVATE);
        currentAdminId = prefs.getInt("user_id", -1);
        String role = prefs.getString("role", "user");

        if (currentAdminId == -1 || !role.equals("admin")) {
            Toast.makeText(this, "Error: Acceso no autorizado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupListeners();
        loadUsersList();
        setupAutoRefresh();
    }

    private void initViews() {
        usersRecyclerView = findViewById(R.id.users_recycler_view);
        loadingProgress = findViewById(R.id.loading_progress);
        emptyState = findViewById(R.id.empty_state);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupRecyclerView() {
        usersList = new ArrayList<>();
        usersAdapter = new ChatUsersAdapter(usersList, this::onUserClick);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        usersRecyclerView.setLayoutManager(layoutManager);
        usersRecyclerView.setAdapter(usersAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadUsersList() {
        loadingProgress.setVisibility(View.VISIBLE);
        emptyState.setVisibility(View.GONE);

        new Thread(() -> {
            try {
                JSONObject response = MessagesAPI.getUsersListForAdmin(currentAdminId);

                runOnUiThread(() -> {
                    loadingProgress.setVisibility(View.GONE);
                    if (response != null && response.optBoolean("success", false)) {
                        parseUsersList(response);
                    } else {
                        String error = response != null ? response.optString("message", "Error al cargar usuarios") : "Error de conexión";
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                        showEmptyState();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    loadingProgress.setVisibility(View.GONE);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    showEmptyState();
                });
            }
        }).start();
    }

    private void parseUsersList(JSONObject response) {
        try {
            JSONArray usersArray = response.getJSONArray("users");
            List<ChatUser> newUsers = new ArrayList<>();

            for (int i = 0; i < usersArray.length(); i++) {
                JSONObject userObj = usersArray.getJSONObject(i);
                ChatUser user = new ChatUser();
                user.setUserId(userObj.optInt("user_id"));
                user.setEmail(userObj.optString("email"));
                user.setName(userObj.optString("name"));
                user.setRole(userObj.optString("role"));
                user.setLastMessage(userObj.optString("last_message"));
                user.setLastMessageTime(userObj.optString("last_message_time"));
                user.setLastMessageFromUser(userObj.optBoolean("is_last_message_from_user"));
                user.setUnreadCount(userObj.optInt("unread_count"));

                newUsers.add(user);
            }

            if (newUsers.isEmpty()) {
                showEmptyState();
            } else {
                usersAdapter.updateUsers(newUsers);
                usersRecyclerView.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar usuarios", Toast.LENGTH_SHORT).show();
            showEmptyState();
        }
    }

    private void onUserClick(ChatUser user) {
        // Abrir chat individual con este usuario
        Intent intent = new Intent(this, AdminChatActivity.class);
        intent.putExtra("user_id", user.getUserId());
        intent.putExtra("user_name", user.getName());
        startActivity(intent);
    }

    private void showEmptyState() {
        usersRecyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.VISIBLE);
    }

    private void setupAutoRefresh() {
        autoRefreshHandler = new Handler(Looper.getMainLooper());
        autoRefreshRunnable = new Runnable() {
            @Override
            public void run() {
                loadUsersListQuietly();
                autoRefreshHandler.postDelayed(this, REFRESH_INTERVAL);
            }
        };
        autoRefreshHandler.postDelayed(autoRefreshRunnable, REFRESH_INTERVAL);
    }

    private void loadUsersListQuietly() {
        // Cargar lista sin mostrar loading
        new Thread(() -> {
            try {
                JSONObject response = MessagesAPI.getUsersListForAdmin(currentAdminId);
                if (response != null && response.optBoolean("success", false)) {
                    runOnUiThread(() -> parseUsersList(response));
                }
            } catch (Exception e) {
                // Ignorar errores en refresh automático
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar al volver a esta pantalla
        loadUsersList();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (autoRefreshHandler != null && autoRefreshRunnable != null) {
            autoRefreshHandler.removeCallbacks(autoRefreshRunnable);
        }
    }
}

