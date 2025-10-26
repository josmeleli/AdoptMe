package com.example.adoptmev5.ui.search;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.adoptmev5.R;
import com.example.adoptmev5.api.AdoptionApiService;
import com.example.adoptmev5.ui.adoption.AdoptionFormActivity;

public class PetDetailActivity extends AppCompatActivity {

    private static final String TAG = "PetDetailActivity";
    private String petId;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        ImageView petImage = findViewById(R.id.pet_image);
        TextView petName = findViewById(R.id.pet_name);
        TextView petDescription = findViewById(R.id.pet_description);
        Button btnAdoptNow = findViewById(R.id.btn_adopt_now);

        // Nuevos campos
        TextView petAge = findViewById(R.id.pet_age);
        TextView petWeight = findViewById(R.id.pet_weight);
        TextView petHeight = findViewById(R.id.pet_height);

        // Obtener userId de SharedPreferences
        SharedPreferences prefs = getSharedPreferences("adoptme_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", 0);

        // Recibimos datos desde el intent
        petId = getIntent().getStringExtra("pet_id");

        // Log de depuración
        Log.d(TAG, "userId: " + userId);
        Log.d(TAG, "petId recibido: " + petId);

        String name = getIntent().getStringExtra("pet_name");
        String description = getIntent().getStringExtra("pet_description");
        String fotoUrl = getIntent().getStringExtra("pet_foto_url");
        String especie = getIntent().getStringExtra("pet_especie");
        int imageRes = getIntent().getIntExtra("pet_image", R.drawable.search1);

        // Extras nuevos
        String age = getIntent().getStringExtra("pet_age");
        String weight = getIntent().getStringExtra("pet_weight");
        String height = getIntent().getStringExtra("pet_height");

        // Setear valores
        petName.setText(name);
        petDescription.setText(description);

        // Cargar imagen desde URL usando Glide
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            Glide.with(this)
                    .load(fotoUrl)
                    .placeholder(getPlaceholderByEspecie(especie))
                    .error(getPlaceholderByEspecie(especie))
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(petImage);
        } else {
            // Fallback a imagen estática si no hay URL
            petImage.setImageResource(imageRes);
        }

        if (age != null) petAge.setText(age);
        if (weight != null) petWeight.setText(weight);
        if (height != null) petHeight.setText(height);

        // Configurar botón de adoptar
        btnAdoptNow.setOnClickListener(v -> {
            Log.d(TAG, "Botón Adoptar presionado");
            Log.d(TAG, "userId actual: " + userId);
            Log.d(TAG, "petId actual: " + petId);

            if (userId == 0) {
                Toast.makeText(this, "Debes iniciar sesión para adoptar", Toast.LENGTH_SHORT).show();
                return;
            }

            if (petId == null || petId.isEmpty()) {
                Log.e(TAG, "ERROR: petId es null o vacío");
                Toast.makeText(this, "Error: ID de mascota no válido (petId: " + petId + ")", Toast.LENGTH_LONG).show();
                return;
            }

            // Verificar si puede aplicar
            checkCanApply();
        });
    }

    /**
     * Verificar si el usuario puede aplicar a esta mascota
     */
    private void checkCanApply() {
        // Convertir petId a int para la API
        int petIdInt;
        try {
            petIdInt = Integer.parseInt(petId);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Error: ID de mascota inválido", Toast.LENGTH_SHORT).show();
            return;
        }

        AdoptionApiService.checkActiveRequest(userId, petIdInt, new AdoptionApiService.CheckRequestCallback() {
            @Override
            public void onSuccess(boolean canApply, String message, boolean hasActiveRequest) {
                runOnUiThread(() -> {
                    if (canApply) {
                        // Abrir formulario de adopción
                        Intent intent = new Intent(PetDetailActivity.this, AdoptionFormActivity.class);
                        intent.putExtra("user_id", userId);
                        intent.putExtra("pet_id", petIdInt);
                        intent.putExtra("pet_name", getIntent().getStringExtra("pet_name"));
                        startActivity(intent);
                    } else {
                        Toast.makeText(PetDetailActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(PetDetailActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    /**
     * Obtener placeholder según la especie
     */
    private int getPlaceholderByEspecie(String especie) {
        if (especie == null) return R.drawable.search1;

        if (especie.equalsIgnoreCase("Perro")) {
            return R.drawable.search1;
        } else if (especie.equalsIgnoreCase("Gato")) {
            return R.drawable.search2;
        } else if (especie.equalsIgnoreCase("Ave")) {
            return R.drawable.search5;
        } else if (especie.equalsIgnoreCase("Conejo")) {
            return R.drawable.search3;
        } else {
            return R.drawable.search1;
        }
    }
}
