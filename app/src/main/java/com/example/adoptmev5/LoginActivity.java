package com.example.adoptmev5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2/adopciones_api"; // Cambia a la URL de tu API si no usas emulador

    private Button buttonLogin;
    private TextView textViewRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencia al botón de login
        buttonLogin = findViewById(R.id.button_login);
        // Referencia al texto "Regístrate"
        textViewRegister = findViewById(R.id.textView_register);

        // Referencias a inputs
        editTextEmail = findViewById(R.id.editText_email);
        editTextPassword = findViewById(R.id.editText_password);

        // Acción al hacer clic en "INICIAR SESIÓN"
        buttonLogin.setOnClickListener(v -> attemptLogin());

        // Acción al hacer clic en "Regístrate"
        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void attemptLogin() {
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingresa email y contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        // Petición en background
        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("email", email);
                body.put("password", password);

                JSONObject resp = postJson(BASE_URL + "/login.php", body);
                if (resp == null) {
                    showToast("Error en la conexión");
                    return;
                }

                boolean success = resp.optBoolean("success", false);
                if (success) {
                    String token = resp.optString("token", "");
                    JSONObject user = resp.optJSONObject("user");

                    if (user != null) {
                        int userId = user.optInt("id", -1);
                        String nombres = user.optString("nombres", "");
                        String apellidos = user.optString("apellidos", "");
                        String userEmail = user.optString("email", "");
                        String telefono = user.optString("telefono", "");
                        String dni = user.optString("dni", "");

                        // Guardar todos los datos del usuario en SharedPreferences
                        SharedPreferences prefs = getSharedPreferences("adoptme_prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putString("token", token);
                        editor.putInt("user_id", userId);
                        editor.putString("nombres", nombres);
                        editor.putString("apellidos", apellidos);
                        editor.putString("email", userEmail);
                        editor.putString("telefono", telefono);
                        editor.putString("dni", dni);
                        editor.apply();

                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Login exitoso", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, NavbarActivity.class);
                            startActivity(intent);
                            finish();
                        });
                    } else {
                        showToast("Error: No se recibieron datos del usuario");
                    }
                } else {
                    String msg = resp.optString("message", "Credenciales inválidas");
                    showToast(msg);
                }

            } catch (java.net.UnknownHostException e) {
                e.printStackTrace();
                showToast("Error: No se puede conectar al servidor. Verifica que tu API esté corriendo.");
            } catch (java.io.IOException e) {
                e.printStackTrace();
                String errorMsg = e.getMessage();
                if (errorMsg != null && errorMsg.contains("Cleartext HTTP")) {
                    showToast("Error: HTTP no permitido. Desinstala la app del emulador e instala de nuevo.");
                } else {
                    showToast("Error de conexión: " + errorMsg);
                }
            } catch (Exception e) {
                e.printStackTrace();
                showToast("Error: " + e.getMessage());
            }
        }).start();
    }

    private JSONObject postJson(String urlString, JSONObject payload) throws IOException, JSONException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes("UTF-8"));
                os.flush();
            }

            int code = conn.getResponseCode();
            InputStreamReader isr = new InputStreamReader(code >= 400 ? conn.getErrorStream() : conn.getInputStream(), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            String respStr = sb.toString();
            if (respStr.isEmpty()) return null;
            return new JSONObject(respStr);
        } finally {
            conn.disconnect();
        }
    }

    private void showToast(String msg) {
        runOnUiThread(() -> Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_LONG).show());
    }
}
