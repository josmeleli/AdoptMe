package com.example.adoptmev5.ui.search;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

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
        int imageRes = getIntent().getIntExtra("pet_image", R.drawable.search1);

        // Extras nuevos
        String age = getIntent().getStringExtra("pet_age");
        String weight = getIntent().getStringExtra("pet_weight");
        String height = getIntent().getStringExtra("pet_height");

        // Setear valores
        petName.setText(name);
        petDescription.setText(description);
        petImage.setImageResource(imageRes);

        if (age != null) petAge.setText(age);
        if (weight != null) petWeight.setText(weight);
        if (height != null) petHeight.setText(height);
    }
}
