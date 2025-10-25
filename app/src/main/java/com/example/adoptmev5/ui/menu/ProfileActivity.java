package com.example.adoptmev5.ui.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adoptmev5.LoginActivity;
import com.example.adoptmev5.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2/adopciones_api";

    private TextView textViewUserName, textViewProfileStatus;
    private ImageView imageViewEditProfile;
    private LinearLayout optionCompatibility, optionPreferences, optionMyProfile,
            optionSettings, optionHelp, optionAbout, optionLogout;

    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener user_id
        SharedPreferences prefs = getSharedPreferences("adoptme_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        // Inicializar vistas
        textViewUserName = findViewById(R.id.textView_user_name);
        textViewProfileStatus = findViewById(R.id.textView_profile_status);
        imageViewEditProfile = findViewById(R.id.imageView_edit_profile);

        optionCompatibility = findViewById(R.id.option_compatibility);
        optionPreferences = findViewById(R.id.option_preferences);
        optionMyProfile = findViewById(R.id.option_my_profile);
        optionSettings = findViewById(R.id.option_settings);
        optionHelp = findViewById(R.id.option_help);
        optionAbout = findViewById(R.id.option_about);
        optionLogout = findViewById(R.id.option_logout);

        // Cargar datos del usuario
        loadUserData();

        // Click en el lápiz de editar
        imageViewEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Listeners de las opciones
        optionCompatibility.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CompatibilityActivity.class);
            startActivity(intent);
        });

        optionPreferences.setOnClickListener(v -> {
            // Abrir pantalla de preferencias (FilterActivity)
            Intent intent = new Intent(ProfileActivity.this, FilterActivity.class);
            startActivity(intent);
        });

        optionMyProfile.setOnClickListener(v -> {
            // Abrir edición de perfil
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        optionSettings.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        optionHelp.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HelpActivity.class);
            startActivity(intent);
        });

        optionAbout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        optionLogout.setOnClickListener(v -> logout());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar datos cuando vuelva de EditProfileActivity
        loadUserData();
    }

    private void loadUserData() {
        if (userId == -1) {
            textViewUserName.setText("Usuario AdoptMe");
            textViewProfileStatus.setText("Inicia sesión para ver tu perfil");
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "/users/getUser.php?user_id=" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int code = conn.getResponseCode();
                if (code == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();

                    JSONObject response = new JSONObject(sb.toString());

                    // Actualizar UI
                    runOnUiThread(() -> {
                        String nombres = response.optString("nombres", "");
                        String apellidos = response.optString("apellidos", "");
                        String fullName = (nombres + " " + apellidos).trim();

                        if (fullName.isEmpty()) {
                            fullName = response.optString("email", "Usuario AdoptMe");
                        }

                        textViewUserName.setText(fullName);
                        textViewProfileStatus.setText("Ver perfil completo");
                    });
                } else {
                    runOnUiThread(() -> {
                        textViewUserName.setText("Usuario AdoptMe");
                        textViewProfileStatus.setText("Error al cargar perfil");
                    });
                }
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    textViewUserName.setText("Usuario AdoptMe");
                    textViewProfileStatus.setText("Error de conexión");
                });
            }
        }).start();
    }

    private void logout() {
        // Limpiar SharedPreferences
        SharedPreferences prefs = getSharedPreferences("adoptme_prefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Ir a LoginActivity
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}