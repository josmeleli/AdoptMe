package com.example.adoptmev5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.adoptmev5.api.NotificationApiService;
import com.example.adoptmev5.databinding.ActivityNavbarBinding;
import com.example.adoptmev5.models.Notification;
import com.example.adoptmev5.ui.notifications.NotificationsActivity;

import java.util.List;

public class NavbarActivity extends AppCompatActivity {

    private static final String TAG = "NavbarActivity";
    private ActivityNavbarBinding binding;
    private TextView toolbarTitle;
    private TextView notificationBadge;
    private int notificationCount = 0;
    private int userId;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavbarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtener datos del usuario
        SharedPreferences prefs = getSharedPreferences("adoptme_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", 0);
        userRole = prefs.getString("user_role", "user");

        Log.d(TAG, "Usuario: " + userId + ", Role: " + userRole);

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Referencias a vistas del AppBar
        toolbarTitle = findViewById(R.id.toolbar_title);
        ImageButton btnNotifications = findViewById(R.id.btn_notifications);
        notificationBadge = findViewById(R.id.notification_badge);

        // Configurar botón de notificaciones
        btnNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(NavbarActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Configuración de destinos principales
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_favorites,
                R.id.navigation_maps, R.id.navigation_menu)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_navbar);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // Listener para cambiar el título según el fragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int destId = destination.getId();
            if (destId == R.id.navigation_home) {
                toolbarTitle.setText("Inicio");
            } else if (destId == R.id.navigation_search) {
                toolbarTitle.setText("Buscar Mascotas");
            } else if (destId == R.id.navigation_favorites) {
                toolbarTitle.setText("Mis Solicitudes");
            } else if (destId == R.id.navigation_maps) {
                toolbarTitle.setText("Mapa");
            } else if (destId == R.id.navigation_menu) {
                toolbarTitle.setText("Menú");
            } else {
                toolbarTitle.setText("AdoptMe");
            }
        });

        // Cargar notificaciones si es admin
        loadNotifications();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar notificaciones al volver
        loadNotifications();
    }

    /**
     * Cargar notificaciones desde el API
     */
    private void loadNotifications() {
        if (userId == 0) return;

        // Solo cargar notificaciones para admins
        if (!"admin".equals(userRole)) {
            updateNotificationBadge(0);
            return;
        }

        NotificationApiService.getNotifications(userId, new NotificationApiService.NotificationsCallback() {
            @Override
            public void onSuccess(List<Notification> notifications) {
                // Contar solo las no leídas
                int unreadCount = 0;
                for (Notification n : notifications) {
                    if (!n.isRead()) {
                        unreadCount++;
                    }
                }

                Log.d(TAG, "Notificaciones no leídas: " + unreadCount);

                int finalUnreadCount = unreadCount;
                runOnUiThread(() -> updateNotificationBadge(finalUnreadCount));
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error al cargar notificaciones: " + error);
                runOnUiThread(() -> updateNotificationBadge(0));
            }
        });
    }

    /**
     * Actualizar el badge de notificaciones
     */
    public void updateNotificationBadge(int count) {
        notificationCount = count;
        if (count > 0) {
            notificationBadge.setVisibility(View.VISIBLE);
            notificationBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            notificationBadge.setVisibility(View.GONE);
        }
    }
}