package com.example.adoptmev5.ui.search;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.adoptmev5.R;

public class PetDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_detail);

        ImageView petImage = findViewById(R.id.pet_image);
        TextView petName = findViewById(R.id.pet_name);
        TextView petDescription = findViewById(R.id.pet_description);

        // Nuevos campos
        TextView petAge = findViewById(R.id.pet_age);
        TextView petWeight = findViewById(R.id.pet_weight);
        TextView petHeight = findViewById(R.id.pet_height);

        // Recibimos datos desde el intent
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
