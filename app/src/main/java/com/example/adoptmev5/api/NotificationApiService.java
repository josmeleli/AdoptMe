package com.example.adoptmev5.api;

import android.util.Log;

import com.example.adoptmev5.models.Notification;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class NotificationApiService {
    private static final String TAG = "NotificationAPI";
    private static final String BASE_URL = "http://10.0.2.2/adopciones_api";

    public interface NotificationsCallback {
        void onSuccess(List<Notification> notifications);
        void onError(String error);
    }

    /**
     * Obtener notificaciones del usuario
     */
    public static void getNotifications(int userId, NotificationsCallback callback) {
        new Thread(() -> {
            try {
                String urlString = BASE_URL + "/notifications/getNotifications.php?user_id=" + userId;
                Log.d(TAG, "Obteniendo notificaciones: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

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

                if (responseCode != HttpURLConnection.HTTP_OK) {
                    callback.onError("Error HTTP " + responseCode + ": " + response.toString());
                    conn.disconnect();
                    return;
                }

                // Parsear respuesta JSON
                JSONArray jsonArray = new JSONArray(response.toString());
                List<Notification> notifications = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    Notification notification = new Notification();

                    notification.setId(json.optInt("id", 0));
                    notification.setUserId(json.optInt("user_id", 0));
                    notification.setType(json.optString("type", ""));
                    notification.setTitle(json.optString("title", ""));
                    notification.setMessage(json.optString("message", ""));
                    notification.setIsRead(json.optInt("is_read", 0));
                    notification.setRelatedId(json.optInt("related_id", 0));
                    notification.setCreatedAt(json.optString("created_at", ""));

                    notifications.add(notification);
                }

                callback.onSuccess(notifications);
                conn.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "Error al obtener notificaciones: ", e);
                callback.onError("Error: " + e.getMessage());
            }
        }).start();
    }

    /**
     * Marcar notificación como leída
     */
    public static void markAsRead(int notificationId, NotificationsCallback callback) {
        new Thread(() -> {
            try {
                String urlString = BASE_URL + "/notifications/markAsRead.php?id=" + notificationId;
                Log.d(TAG, "Marcando como leída: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                int responseCode = conn.getResponseCode();
                Log.d(TAG, "Response code: " + responseCode);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Log.d(TAG, "Response: " + response.toString());

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    callback.onSuccess(new ArrayList<>());
                } else {
                    callback.onError("Error al marcar como leída");
                }

                conn.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "Error al marcar como leída: ", e);
                callback.onError("Error: " + e.getMessage());
            }
        }).start();
    }
}

