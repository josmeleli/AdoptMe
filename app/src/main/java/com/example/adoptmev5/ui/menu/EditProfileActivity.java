package com.example.adoptmev5.ui.menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adoptmev5.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class EditProfileActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://10.0.2.2/adopciones_api";

    private EditText editTextNombres, editTextApellidos, editTextTelefono, editTextDistrito;
    private Spinner spinnerEspecie, spinnerTamano, spinnerEdad;
    private Button buttonSave, buttonCancel;

    private int userId = -1;

    // Variables para almacenar datos originales
    private String originalNombres = "";
    private String originalApellidos = "";
    private String originalTelefono = "";
    private String originalDistrito = "";
    private String originalEspecie = "";
    private String originalTamano = "";
    private String originalEdad = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Obtener user_id de SharedPreferences
        SharedPreferences prefs = getSharedPreferences("adoptme_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        if (userId == -1) {
            Toast.makeText(this, "Error: No hay sesión activa", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar vistas
        editTextNombres = findViewById(R.id.editText_nombres);
        editTextApellidos = findViewById(R.id.editText_apellidos);
        editTextTelefono = findViewById(R.id.editText_telefono);
        editTextDistrito = findViewById(R.id.editText_distrito);

        spinnerEspecie = findViewById(R.id.spinner_especie);
        spinnerTamano = findViewById(R.id.spinner_tamano);
        spinnerEdad = findViewById(R.id.spinner_edad);

        buttonSave = findViewById(R.id.button_save);
        buttonCancel = findViewById(R.id.button_cancel);

        // Configurar spinners
        setupSpinners();

        // Cargar datos del usuario
        loadUserData();

        // Listeners
        buttonSave.setOnClickListener(v -> saveProfile());
        buttonCancel.setOnClickListener(v -> finish());
    }

    private void setupSpinners() {
        // Especie
        String[] especies = {"Seleccionar", "Perro", "Gato", "Ambos"};
        ArrayAdapter<String> adapterEspecie = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, especies);
        adapterEspecie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEspecie.setAdapter(adapterEspecie);

        // Tamaño
        String[] tamanos = {"Seleccionar", "Pequeño", "Mediano", "Grande"};
        ArrayAdapter<String> adapterTamano = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tamanos);
        adapterTamano.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTamano.setAdapter(adapterTamano);

        // Edad
        String[] edades = {"Seleccionar", "Cachorro", "Adulto", "Senior"};
        ArrayAdapter<String> adapterEdad = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, edades);
        adapterEdad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEdad.setAdapter(adapterEdad);
    }

    private void loadUserData() {
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

                    // Actualizar UI en el hilo principal
                    runOnUiThread(() -> populateFields(response));
                } else {
                    runOnUiThread(() -> Toast.makeText(EditProfileActivity.this,
                            "Error al cargar datos", Toast.LENGTH_SHORT).show());
                }
                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(EditProfileActivity.this,
                        "Error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        }).start();
    }

    private void populateFields(JSONObject data) {
        try {
            // Datos básicos
            String nombres = data.optString("nombres", "");
            String apellidos = data.optString("apellidos", "");
            String telefono = data.optString("telefono", "");
            String distrito = data.optString("distrito", "");

            // Guardar valores originales
            originalNombres = nombres;
            originalApellidos = apellidos;
            originalTelefono = telefono;
            originalDistrito = distrito;

            editTextNombres.setText(nombres);
            editTextApellidos.setText(apellidos);
            editTextTelefono.setText(telefono);
            editTextDistrito.setText(distrito);

            // Preferencias (pueden estar en un objeto anidado "preferencias" o directamente)
            JSONObject prefs = data.optJSONObject("preferencias");
            String especie = "";
            String tamano = "";
            String edad = "";

            if (prefs != null) {
                especie = prefs.optString("especie", "");
                tamano = prefs.optString("tamano", "");
                edad = prefs.optString("edad", "");
            } else {
                especie = data.optString("especie_preferida", "");
                tamano = data.optString("tamano_preferido", "");
                edad = data.optString("edad_preferida", "");
            }

            // Guardar preferencias originales
            originalEspecie = especie;
            originalTamano = tamano;
            originalEdad = edad;

            // Seleccionar en spinners
            setSpinnerSelection(spinnerEspecie, especie);
            setSpinnerSelection(spinnerTamano, tamano);
            setSpinnerSelection(spinnerEdad, edad);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
        }
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null || value.isEmpty()) return;

        ArrayAdapter adapter = (ArrayAdapter) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void saveProfile() {
        final String nombres = editTextNombres.getText().toString().trim();
        final String apellidos = editTextApellidos.getText().toString().trim();
        final String telefono = editTextTelefono.getText().toString().trim();
        final String distrito = editTextDistrito.getText().toString().trim();

        final String especie = spinnerEspecie.getSelectedItemPosition() > 0 ?
                spinnerEspecie.getSelectedItem().toString() : "";
        final String tamano = spinnerTamano.getSelectedItemPosition() > 0 ?
                spinnerTamano.getSelectedItem().toString() : "";
        final String edad = spinnerEdad.getSelectedItemPosition() > 0 ?
                spinnerEdad.getSelectedItem().toString() : "";

        // Verificar si hubo cambios
        boolean hayCambios = false;

        if (!nombres.equals(originalNombres)) hayCambios = true;
        if (!apellidos.equals(originalApellidos)) hayCambios = true;
        if (!telefono.equals(originalTelefono)) hayCambios = true;
        if (!distrito.equals(originalDistrito)) hayCambios = true;
        if (!especie.equals(originalEspecie)) hayCambios = true;
        if (!tamano.equals(originalTamano)) hayCambios = true;
        if (!edad.equals(originalEdad)) hayCambios = true;

        if (!hayCambios) {
            Toast.makeText(this, "No hay cambios para guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar solo los campos que fueron modificados
        if (!nombres.equals(originalNombres) && nombres.isEmpty()) {
            Toast.makeText(this, "Los nombres no pueden estar vacíos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!apellidos.equals(originalApellidos) && apellidos.isEmpty()) {
            Toast.makeText(this, "Los apellidos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!telefono.equals(originalTelefono) && !telefono.isEmpty() && telefono.length() != 9) {
            Toast.makeText(this, "El teléfono debe tener 9 dígitos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar progress
        buttonSave.setEnabled(false);
        buttonSave.setText("GUARDANDO...");

        new Thread(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("user_id", userId);

                // Solo enviar los campos que fueron modificados
                if (!nombres.equals(originalNombres)) {
                    body.put("nombres", nombres);
                }
                if (!apellidos.equals(originalApellidos)) {
                    body.put("apellidos", apellidos);
                }
                if (!telefono.equals(originalTelefono)) {
                    body.put("telefono", telefono);
                }
                if (!distrito.equals(originalDistrito)) {
                    body.put("distrito", distrito);
                }
                if (!especie.equals(originalEspecie)) {
                    body.put("especie_preferida", especie);
                }
                if (!tamano.equals(originalTamano)) {
                    body.put("tamano_preferido", tamano);
                }
                if (!edad.equals(originalEdad)) {
                    body.put("edad_preferida", edad);
                }

                JSONObject response = putJson(BASE_URL + "/users/updateUser.php", body);

                runOnUiThread(() -> {
                    buttonSave.setEnabled(true);
                    buttonSave.setText("GUARDAR CAMBIOS");

                    if (response != null && response.optBoolean("success", false)) {
                        String message = response.optString("message", "Perfil actualizado");
                        Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_LONG).show();
                        finish(); // Volver a ProfileActivity
                    } else {
                        String errorMsg = response != null ?
                                response.optString("error", response.optString("message", "Error al actualizar")) :
                                "Error de conexión";
                        Toast.makeText(EditProfileActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    buttonSave.setEnabled(true);
                    buttonSave.setText("GUARDAR CAMBIOS");
                    Toast.makeText(EditProfileActivity.this,
                            "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private JSONObject putJson(String urlString, JSONObject payload) throws IOException, JSONException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(payload.toString().getBytes("UTF-8"));
                os.flush();
            }

            int code = conn.getResponseCode();
            InputStreamReader isr = new InputStreamReader(
                    code >= 400 ? conn.getErrorStream() : conn.getInputStream(), "UTF-8");
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

