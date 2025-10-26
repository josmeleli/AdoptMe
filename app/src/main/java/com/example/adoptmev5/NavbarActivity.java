package com.example.adoptmev5;
import com.example.adoptmev5.ui.menu.ThemeActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.adoptmev5.databinding.ActivityNavbarBinding;

public class NavbarActivity extends AppCompatActivity {

    private ActivityNavbarBinding binding;
    private TextView toolbarTitle;
    private ImageButton btnNotifications;
    private TextView notificationBadge;
    private int notificationCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNavbarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Configurar Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false); // Ocultar título por defecto
        }

        // Referencias a vistas del AppBar
        toolbarTitle = findViewById(R.id.toolbar_title);
        btnNotifications = findViewById(R.id.btn_notifications);
        notificationBadge = findViewById(R.id.notification_badge);

        // Configurar botón de notificaciones
        btnNotifications.setOnClickListener(v -> {
            Toast.makeText(this, "Notificaciones (" + notificationCount + ")", Toast.LENGTH_SHORT).show();
            // TODO: Abrir activity de notificaciones
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

        // Inicialmente sin notificaciones (se actualizará desde el API)
        updateNotificationBadge(0);
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