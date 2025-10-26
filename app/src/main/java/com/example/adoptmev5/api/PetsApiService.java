package com.example.adoptmev5.api;

import android.util.Log;

import com.example.adoptmev5.models.Pet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PetsApiService {
    private static final String TAG = "PetsApiService";
    // Base local del emulator para tu backend PHP
    private static final String BASE_URL = "http://10.0.2.2/adopciones_api";

    public interface PetsCallback {
        void onSuccess(List<Pet> pets, JSONObject pagination);
        void onError(String error);
    }

    public static void getPets(Map<String, String> filters, PetsCallback callback) {
        new Thread(() -> {
            try {
                // Construir URL con parámetros apuntando a tu endpoint PHP
                StringBuilder urlBuilder = new StringBuilder(BASE_URL + "/pets/getPets.php");

                if (filters != null && !filters.isEmpty()) {
                    StringBuilder q = new StringBuilder();
                    for (Map.Entry<String, String> entry : filters.entrySet()) {
                        String k = entry.getKey();
                        String v = entry.getValue();
                        if (v == null || v.isEmpty()) continue;
                        if (q.length() > 0) q.append('&');
                        q.append(java.net.URLEncoder.encode(k, "UTF-8"))
                                .append('=')
                                .append(java.net.URLEncoder.encode(v, "UTF-8"));
                    }
                    if (q.length() > 0) {
                        urlBuilder.append('?').append(q);
                    }
                }

                String urlString = urlBuilder.toString();
                Log.d(TAG, "Llamando API: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    // Parsear respuesta JSON
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONArray petsArray = jsonResponse.optJSONArray("data");
                    JSONObject pagination = jsonResponse.optJSONObject("pagination");

                    List<Pet> pets = new ArrayList<>();
                    if (petsArray != null) {
                        for (int i = 0; i < petsArray.length(); i++) {
                            JSONObject petJson = petsArray.getJSONObject(i);
                            Pet pet = parsePetFromJson(petJson);
                            if (pet != null) {
                                pets.add(pet);
                            }
                        }
                    }

                    Log.d(TAG, "Mascotas cargadas: " + pets.size());
                    callback.onSuccess(pets, pagination);

                } else {
                    String errorMsg = "Error HTTP: " + responseCode;
                    Log.e(TAG, errorMsg);
                    callback.onError(errorMsg);
                }

                connection.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "Error en getPets: " + e.getMessage(), e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    private static Pet parsePetFromJson(JSONObject json) {
        try {
            Pet pet = new Pet();

            // Asignar id como String (coincide con Pet.setId)
            String idStr = json.optString("id", json.optString("_id", ""));
            pet.setId(idStr);

            // Nombre (soporta 'name' o 'nombre')
            String name = json.optString("name", json.optString("nombre", "Sin nombre"));
            pet.setName(name);

            pet.setEspecie(json.optString("especie", json.optString("type", "")));
            pet.setRaza(json.optString("raza", json.optString("breed", "")));
            pet.setEdad(json.optInt("edad", json.optInt("age", 0)));
            pet.setSexo(json.optString("sexo", json.optString("gender", "")));
            pet.setTamano(json.optString("tamano", json.optString("size", "")));
            pet.setDescripcion(json.optString("descripcion", json.optString("description", "")));
            pet.setDistrito(json.optString("distrito", json.optString("district", "")));
            // campo urgente / is_urgent / isUrgent
            pet.setUrgent(json.optBoolean("is_urgent", json.optBoolean("urgente", false)));

            // Fotos: preferir 'foto_url' (según tu doc) o 'foto' en array o campo
            String foto = json.optString("foto_url", "");
            if (foto.isEmpty()) {
                if (json.has("foto")) {
                    foto = json.optString("foto", "");
                } else {
                    JSONArray fotosArray = json.optJSONArray("fotos");
                    if (fotosArray != null && fotosArray.length() > 0) {
                        foto = fotosArray.optString(0, "");
                    }
                }
            }
            pet.setFotoUrl(foto);

            return pet;

        } catch (Exception e) {
            Log.e(TAG, "Error parseando mascota: " + e.getMessage(), e);
            return null;
        }
    }

    public static void getPetById(String petId, PetDetailCallback callback) {
        new Thread(() -> {
            try {
                // endpoint de detalles según tu doc
                String urlString = BASE_URL + "/pets/getPetDetails.php?pet_id=" + java.net.URLEncoder.encode(petId, "UTF-8");
                Log.d(TAG, "Obteniendo detalle de mascota: " + urlString);

                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject jsonResponse = new JSONObject(response.toString());
                    JSONObject petJson = jsonResponse.optJSONObject("data");
                    Pet pet = petJson != null ? parsePetFromJson(petJson) : null;

                    if (pet != null) {
                        callback.onSuccess(pet);
                    } else {
                        callback.onError("Error al parsear mascota");
                    }

                } else {
                    callback.onError("Error HTTP: " + responseCode);
                }

                connection.disconnect();

            } catch (Exception e) {
                Log.e(TAG, "Error en getPetById: " + e.getMessage(), e);
                callback.onError(e.getMessage());
            }
        }).start();
    }

    public interface PetDetailCallback {
        void onSuccess(Pet pet);
        void onError(String error);
    }
}
