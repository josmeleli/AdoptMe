package com.example.adoptmev5.api;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdoptionApiService {
    private static final String TAG = "AdoptionApiService";
    private static final String BASE_URL = "http://10.0.2.2/adopciones_api";

    public interface CheckRequestCallback {
        void onSuccess(boolean canApply, String message, boolean hasActiveRequest);
        void onError(String error);
    }

    public interface CreateRequestCallback {
        void onSuccess(int requestId, String message);
        void onError(String error);
    }

    /**
     * Verificar si el usuario puede aplicar a una mascota
     */
    public static void checkActiveRequest(int userId, int petId, CheckRequestCallback callback) {
        new Thread(() -> {
            try {
                String urlString = BASE_URL + "/adoptions/checkActiveRequest.php?user_id=" + userId + "&pet_id=" + petId;
                Log.d(TAG, "Verificando solicitud activa: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    Log.d(TAG, "Response: " + response.toString());

                    JSONObject json = new JSONObject(response.toString());
                    boolean success = json.optBoolean("success", false);

                    if (success) {
                        boolean canApply = json.optBoolean("can_apply", false);
                        boolean hasActiveRequest = json.optBoolean("has_active_request", false);
                        String message = json.optString("message", "");
                        callback.onSuccess(canApply, message, hasActiveRequest);
                    } else {
                        callback.onError(json.optString("message", "Error desconocido"));
                    }
                } else {
                    callback.onError("Error HTTP: " + responseCode);
                }
                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error al verificar solicitud: ", e);
                callback.onError("Error: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Crear solicitud de adopción
     */
    public static void createRequest(JSONObject requestData, CreateRequestCallback callback) {
        new Thread(() -> {
            try {
                String urlString = BASE_URL + "/adoptions/createRequest.php";
                Log.d(TAG, "Creando solicitud: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);

                // Enviar datos
                OutputStream os = conn.getOutputStream();
                os.write(requestData.toString().getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                BufferedReader reader;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Log.d(TAG, "Response: " + response.toString());

                JSONObject json = new JSONObject(response.toString());
                boolean success = json.optBoolean("success", false);

                if (success) {
                    int requestId = json.optInt("request_id", 0);
                    String message = json.optString("message", "Solicitud enviada exitosamente");
                    callback.onSuccess(requestId, message);
                } else {
                    String errorMessage = json.optString("message", "Error desconocido");
                    callback.onError(errorMessage);
                }

                conn.disconnect();
            } catch (Exception e) {
                Log.e(TAG, "Error al crear solicitud: ", e);
                callback.onError("Error: " + e.getMessage());
            }
        }).start();
    }
}

