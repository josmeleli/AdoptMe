package com.example.adoptmev5.ui.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adoptmev5.LoginActivity;
import com.example.adoptmev5.R;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {
    private static final String BASE_URL = "http://10.0.2.2/adopciones_api";

    private ImageView imageViewEditProfile;
    private TextView textViewUserName, textViewProfileStatus;
    private LinearLayout optionCompatibility, optionPreferences, optionMyProfile,
            optionSettings, optionHelp, optionAbout, optionLogout;

    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Obtener userId de SharedPreferences
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
        loadUserProfile();

        // Configurar listeners
        optionLogout.setOnClickListener(v -> logout());

        // Otras opciones (implementar según necesites)
        optionMyProfile.setOnClickListener(v ->
            Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
        );

        optionSettings.setOnClickListener(v ->
            Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT).show()
        );

        optionHelp.setOnClickListener(v ->
            Toast.makeText(this, "Ayuda", Toast.LENGTH_SHORT).show()
        );

        optionAbout.setOnClickListener(v ->
            Toast.makeText(this, "Acerca de", Toast.LENGTH_SHORT).show()
        );
    }

    private void loadUserProfile() {
        if (userId == -1) {
            textViewUserName.setText("Usuario AdoptMe");
            textViewProfileStatus.setText("Error: Sesión no válida");
            return;
        }

        new Thread(() -> {
            try {
                URL url = new URL(BASE_URL + "/users/getUser.php?id=" + userId);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());

                    runOnUiThread(() -> {
                        String nombres = jsonResponse.optString("nombres", "");
                        String apellidos = jsonResponse.optString("apellidos", "");
                        String fullName = (nombres + " " + apellidos).trim();

                        if (fullName.isEmpty()) {
                            fullName = jsonResponse.optString("email", "Usuario AdoptMe");
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