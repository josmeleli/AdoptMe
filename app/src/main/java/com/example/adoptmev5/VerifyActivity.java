package com.example.adoptmev5;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class VerifyActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2/adopciones_api";

    private EditText editTextCode;
    private Button buttonVerify, buttonSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        editTextCode = findViewById(R.id.editText_code);
        buttonVerify = findViewById(R.id.button_verify);
        buttonSkip = findViewById(R.id.button_skip);

        buttonVerify.setOnClickListener(v -> attemptVerify());
        buttonSkip.setOnClickListener(v -> {
            startActivity(new Intent(VerifyActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void attemptVerify() {
        final String code = editTextCode.getText().toString().trim();
        if (code.isEmpty()) {
            Toast.makeText(this, "Ingresa el código", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("adoptme_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("pending_user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "No hay usuario pendiente", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("user_id", userId);
                body.put("code", code);

                JSONObject resp = postJson(BASE_URL + "/verify.php", body);
                if (resp == null) {
                    runOnUiThread(() -> Toast.makeText(VerifyActivity.this, "Error en la conexión", Toast.LENGTH_SHORT).show());
                    return;
                }
                boolean success = resp.optBoolean("success", false);
                if (success) {
                    // Verified
                    runOnUiThread(() -> {
                        Toast.makeText(VerifyActivity.this, "Cuenta verificada", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(VerifyActivity.this, LoginActivity.class));
                        finish();
                    });
                } else {
                    String msg = resp.optString("message", "Código inválido");
                    runOnUiThread(() -> Toast.makeText(VerifyActivity.this, msg, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(VerifyActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private JSONObject postJson(String urlString, org.json.JSONObject payload) throws IOException, org.json.JSONException {
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
}

