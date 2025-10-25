package com.example.adoptmev5.ui.menu;

import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.adoptmev5.R;

public class CompatibilityActivity extends AppCompatActivity {

    private int currentQuestion = 1;

    // Layouts de preguntas
    private int[] layouts = new int[]{
            R.layout.activity_compatibility6, // Pregunta 1
            R.layout.activity_compatibility1, // Pregunta 2
            R.layout.activity_compatibility2, // Pregunta 3
            R.layout.activity_compatibility3, // Pregunta 4
            R.layout.activity_compatibility4  // Pregunta 5
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Mostramos primero el layout inicial (Sí / No)
        setContentView(R.layout.activity_compatibility);

        // Botón "Sí, comenzar"
        Button btnStart = findViewById(R.id.btn_start_test);
        btnStart.setOnClickListener(v -> {
            currentQuestion = 1;
            loadQuestion(currentQuestion);
        });

        // Botón "No, gracias"
        Button btnSkip = findViewById(R.id.btn_skip_test);
        btnSkip.setOnClickListener(v -> finish());
    }

    private void loadQuestion(int questionNumber) {
        setContentView(layouts[questionNumber - 1]);
        setupButtons();
    }

    private void setupButtons() {
        Button option1 = findViewById(R.id.btn_option1);
        Button option2 = findViewById(R.id.btn_option2);
        Button option3 = findViewById(R.id.btn_option3);

        if (option1 != null) option1.setOnClickListener(v -> nextQuestion());
        if (option2 != null) option2.setOnClickListener(v -> nextQuestion());
        if (option3 != null) option3.setOnClickListener(v -> nextQuestion());
    }

    private void nextQuestion() {
        if (currentQuestion < layouts.length) {
            currentQuestion++;
            loadQuestion(currentQuestion);
        } else {
            showResults();
        }
    }

    private void showResults() {
        // Aquí puedes abrir otra Activity de resultados
        // o simplemente cerrar esta pantalla
        finish();
    }
}
