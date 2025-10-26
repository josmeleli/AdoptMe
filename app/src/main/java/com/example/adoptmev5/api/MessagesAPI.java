package com.example.adoptmev5.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MessagesAPI {
    private static final String BASE_URL = "http://10.0.2.2/adopciones_api/messages";

    // Enviar mensaje
    public static JSONObject sendMessage(int senderId, String message, Integer receiverId) throws IOException, JSONException {
        String urlString = BASE_URL + "/sendMessage.php";
        JSONObject body = new JSONObject();
        body.put("sender_id", senderId);
        body.put("message", message);
        if (receiverId != null) {
            body.put("receiver_id", receiverId);
        }
        return postJson(urlString, body);
    }

    // Obtener chat para usuario (chat grupal con admins)
    public static JSONObject getChatForUser(int userId, int limit) throws IOException, JSONException {
        String urlString = BASE_URL + "/getChatForUser.php?user_id=" + userId + "&limit=" + limit;
        return getJson(urlString);
    }

    // Obtener lista de usuarios para admin
    public static JSONObject getUsersListForAdmin(int adminId) throws IOException, JSONException {
        String urlString = BASE_URL + "/getUsersListForAdmin.php?admin_id=" + adminId;
        return getJson(urlString);
    }

    // Obtener chat individual para admin con un usuario
    public static JSONObject getChatForAdmin(int adminId, int userId, int limit) throws IOException, JSONException {
        String urlString = BASE_URL + "/getChatForAdmin.php?admin_id=" + adminId + "&user_id=" + userId + "&limit=" + limit;
        return getJson(urlString);
    }

    // Obtener contador de mensajes no leídos
    public static JSONObject getUnreadCount(int userId) throws IOException, JSONException {
        String urlString = BASE_URL + "/getUnreadCount.php?user_id=" + userId;
        return getJson(urlString);
    }

    // Métodos auxiliares HTTP
    private static JSONObject postJson(String urlString, JSONObject payload) throws IOException, JSONException {
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

    private static JSONObject getJson(String urlString) throws IOException, JSONException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("GET");

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

