package com.example.adoptmev5;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Actividad que redirige al chat del usuario.
 * Esta es solo una actividad de redirección para mantener compatibilidad.
 */
public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Redirigir directamente a UserChatActivity
        Intent intent = new Intent(this, UserChatActivity.class);
        startActivity(intent);
        finish();
    }
}

