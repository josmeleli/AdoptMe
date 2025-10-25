package com.example.adoptmev5;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private Button buttonLogin;
    private TextView textViewRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencia al botón de login
        buttonLogin = findViewById(R.id.button_login);
        // Referencia al texto "Regístrate"
        textViewRegister = findViewById(R.id.textView_register);

        // Acción al hacer clic en "INICIAR SESIÓN"
        buttonLogin.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, NavbarActivity.class);
            startActivity(intent);
            finish(); // Opcional: cierra la actividad de login
        });

        // Acción al hacer clic en "Regístrate"
        textViewRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
