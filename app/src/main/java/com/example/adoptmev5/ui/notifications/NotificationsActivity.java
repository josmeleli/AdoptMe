package com.example.adoptmev5.ui.notifications;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptmev5.R;
import com.example.adoptmev5.api.NotificationApiService;
import com.example.adoptmev5.models.Notification;

import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    private static final String TAG = "NotificationsActivity";

    private RecyclerView recyclerNotifications;
    private ProgressBar progressLoading;
    private LinearLayout emptyState;
    private NotificationsAdapter adapter;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Obtener userId de SharedPreferences
        SharedPreferences prefs = getSharedPreferences("adoptme_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", 0);

        // Inicializar vistas
        recyclerNotifications = findViewById(R.id.recycler_notifications);
        progressLoading = findViewById(R.id.progress_loading);
        emptyState = findViewById(R.id.empty_state);

        // Configurar RecyclerView
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationsAdapter(this::onNotificationClick);
        recyclerNotifications.setAdapter(adapter);

        // Cargar notificaciones
        if (userId != 0) {
            loadNotifications();
        } else {
            Toast.makeText(this, "Error: Usuario no encontrado", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadNotifications() {
        Log.d(TAG, "Cargando notificaciones para userId: " + userId);

        progressLoading.setVisibility(View.VISIBLE);
        recyclerNotifications.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);

        NotificationApiService.getNotifications(userId, new NotificationApiService.NotificationsCallback() {
            @Override
            public void onSuccess(List<Notification> notifications) {
                Log.d(TAG, "Notificaciones recibidas: " + notifications.size());

                runOnUiThread(() -> {
                    progressLoading.setVisibility(View.GONE);

                    if (notifications.isEmpty()) {
                        recyclerNotifications.setVisibility(View.GONE);
                        emptyState.setVisibility(View.VISIBLE);
                    } else {
                        recyclerNotifications.setVisibility(View.VISIBLE);
                        emptyState.setVisibility(View.GONE);
                        adapter.setNotifications(notifications);
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error al cargar notificaciones: " + error);

                runOnUiThread(() -> {
                    progressLoading.setVisibility(View.GONE);
                    recyclerNotifications.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);
                    Toast.makeText(NotificationsActivity.this,
                        "Error al cargar notificaciones: " + error,
                        Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void onNotificationClick(Notification notification) {
        Log.d(TAG, "Notificación clickeada: " + notification.getId());

        // Marcar como leída si no está leída
        if (!notification.isRead()) {
            NotificationApiService.markAsRead(notification.getId(),
                new NotificationApiService.NotificationsCallback() {
                    @Override
                    public void onSuccess(List<Notification> notifications) {
                        Log.d(TAG, "Notificación marcada como leída");
                        // Recargar notificaciones
                        loadNotifications();
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error al marcar como leída: " + error);
                    }
                });
        }

        // Aquí puedes abrir la pantalla de detalles según el tipo
        if ("nueva_solicitud".equals(notification.getType())) {
            // TODO: Abrir detalles de la solicitud con notification.getRelatedId()
            Toast.makeText(this, "Abrir solicitud #" + notification.getRelatedId(),
                Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar notificaciones al volver a la pantalla
        if (userId != 0) {
            loadNotifications();
        }
    }
}

