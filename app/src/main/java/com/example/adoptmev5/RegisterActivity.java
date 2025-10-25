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

public class RegisterActivity extends AppCompatActivity {

    private Button buttonRegister;
    private TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Referencias
        buttonRegister = findViewById(R.id.button_register);
        textViewLogin = findViewById(R.id.textView_login);

        // Acción al hacer clic en "REGISTRARSE"
        buttonRegister.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, NavbarActivity.class);
            startActivity(intent);
            finish(); // Cierra RegisterActivity para no volver atrás
        });

        // Acción al hacer clic en "Iniciar Sesión"
        textViewLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Opcional: cierra RegisterActivity
        });
    }
}
