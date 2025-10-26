package com.example.adoptmev5.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MessagesApiService {
    private static final String BASE_URL = "http://10.0.2.2/adopciones_api/messages";
    private RequestQueue requestQueue;

    public MessagesApiService(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    // Interfaz para callbacks
    public interface ApiCallback {
        void onSuccess(JSONObject response);
        void onError(String error);
    }

    /**
     * Enviar mensaje
     * Para USER: solo enviar sender_id y message (va a todos los admins)
     * Para ADMIN: enviar sender_id, receiver_id y message
     */
    public void sendMessage(JSONObject messageData, ApiCallback callback) {
        String url = BASE_URL + "/sendMessage.php";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                messageData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = error.getMessage() != null ? error.getMessage() : "Error al enviar mensaje";
                        callback.onError(errorMsg);
                    }
                }
        );

        requestQueue.add(request);
    }

    /**
     * Obtener chat para usuario (chat grupal con admins)
     */
    public void getChatForUser(int userId, int limit, ApiCallback callback) {
        String url = BASE_URL + "/getChatForUser.php?user_id=" + userId + "&limit=" + limit;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = error.getMessage() != null ? error.getMessage() : "Error al cargar mensajes";
                        callback.onError(errorMsg);
                    }
                }
        );

        requestQueue.add(request);
    }

    /**
     * Obtener lista de usuarios para admin
     */
    public void getUsersListForAdmin(int adminId, ApiCallback callback) {
        String url = BASE_URL + "/getUsersListForAdmin.php?admin_id=" + adminId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = error.getMessage() != null ? error.getMessage() : "Error al cargar usuarios";
                        callback.onError(errorMsg);
                    }
                }
        );

        requestQueue.add(request);
    }

    /**
     * Obtener chat para admin con un usuario específico
     */
    public void getChatForAdmin(int adminId, int userId, int limit, ApiCallback callback) {
        String url = BASE_URL + "/getChatForAdmin.php?admin_id=" + adminId + "&user_id=" + userId + "&limit=" + limit;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = error.getMessage() != null ? error.getMessage() : "Error al cargar conversación";
                        callback.onError(errorMsg);
                    }
                }
        );

        requestQueue.add(request);
    }

    /**
     * Obtener contador de mensajes no leídos
     */
    public void getUnreadCount(int userId, ApiCallback callback) {
        String url = BASE_URL + "/getUnreadCount.php?user_id=" + userId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String errorMsg = error.getMessage() != null ? error.getMessage() : "Error al obtener contador";
                        callback.onError(errorMsg);
                    }
                }
        );

        requestQueue.add(request);
    }
}

