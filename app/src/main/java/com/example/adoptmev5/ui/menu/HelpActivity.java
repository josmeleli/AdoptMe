package com.example.adoptmev5.ui.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adoptmev5.AdminUsersListActivity;
import com.example.adoptmev5.ChatActivity;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtener el rol del usuario
        SharedPreferences prefs = getSharedPreferences("adoptme_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        String userRole = prefs.getString("role", "user");

        if (userId == -1) {
            Toast.makeText(this, "Usuario no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Redirigir según el rol
        Intent intent;
        if ("admin".equals(userRole)) {
            // Admin ve lista de usuarios
            intent = new Intent(this, AdminUsersListActivity.class);
        } else {
            // Usuario ve chat grupal con admins
            intent = new Intent(this, ChatActivity.class);
        }

        startActivity(intent);
        finish(); // Cerrar esta actividad
    }
}

