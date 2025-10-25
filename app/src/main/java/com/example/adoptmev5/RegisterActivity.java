package com.example.adoptmev5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
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

public class RegisterActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2/adopciones_api"; // Cambia si es necesario

    private Button buttonRegister;
    private TextView textViewLogin;
    private EditText editTextNombres, editTextApellidos, editTextDni, editTextEmail, editTextTelefono, editTextPassword;
    private CheckBox checkboxTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias
        buttonRegister = findViewById(R.id.button_register);
        textViewLogin = findViewById(R.id.textView_login);

        editTextNombres = findViewById(R.id.editText_nombres);
        editTextApellidos = findViewById(R.id.editText_apellidos);
        editTextDni = findViewById(R.id.editText_dni);
        editTextEmail = findViewById(R.id.editText_email);
        editTextTelefono = findViewById(R.id.editText_telefono);
        editTextPassword = findViewById(R.id.editText_password);
        checkboxTerms = findViewById(R.id.checkbox_terms);

        // Acción al hacer clic en "REGISTRARSE"
        buttonRegister.setOnClickListener(v -> attemptRegister());

        // Acción al hacer clic en "Iniciar Sesión"
        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void attemptRegister() {
        final String nombres = editTextNombres.getText().toString().trim();
        final String apellidos = editTextApellidos.getText().toString().trim();
        final String dni = editTextDni.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String telefono = editTextTelefono.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if (nombres.isEmpty() || email.isEmpty() || telefono.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Completa los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkboxTerms.isChecked()) {
            Toast.makeText(this, "Debes aceptar los términos y condiciones", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("nombres", nombres);
                body.put("apellidos", apellidos);
                body.put("dni", dni);
                body.put("email", email);
                body.put("telefono", telefono);
                body.put("password", password);

                JSONObject resp = postJson(BASE_URL + "/register.php", body);
                if (resp == null) {
                    showToast("Error en la conexión");
                    return;
                }

                boolean success = resp.optBoolean("success", false);
                if (success) {
                    String userIdStr = resp.optString("user_id", "-1");
                    int userIdTemp = -1;
                    try {
                        userIdTemp = Integer.parseInt(userIdStr);
                    } catch (NumberFormatException e) {
                        // Si no se puede parsear, mantener -1
                    }
                    final int userId = userIdTemp; // Hacer final para usar en lambda

                    final String message = resp.optString("message", "Registrado. Revisa tu correo.");
                    final String verificationCode = resp.optString("verification_code", "");
                    final boolean emailEnviado = resp.optBoolean("email_enviado", false);

                    // Guardar user_id temporalmente
                    SharedPreferences prefs = getSharedPreferences("adoptme_prefs", MODE_PRIVATE);
                    prefs.edit().putInt("pending_user_id", userId).apply();

                    runOnUiThread(() -> {
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

                        // Si el email no se envió, mostrar el código en pantalla (solo desarrollo)
                        if (!emailEnviado && !verificationCode.isEmpty()) {
                            Toast.makeText(RegisterActivity.this,
                                "⚠️ DESARROLLO - Código: " + verificationCode,
                                Toast.LENGTH_LONG).show();
                        }

                        // Abrir pantalla de verificación si hay user_id válido
                        if (userId != -1) {
                            Intent intent = new Intent(RegisterActivity.this, VerifyActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // Si no hay user_id, volver al login
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    // Intentar leer el campo "error" primero, luego "message"
                    String errorMsg = resp.optString("error", "");
                    if (errorMsg.isEmpty()) {
                        errorMsg = resp.optString("message", "Error al registrar");
                    }
                    final String finalErrorMsg = errorMsg;
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, finalErrorMsg, Toast.LENGTH_LONG).show());
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
        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show());
    }
}
