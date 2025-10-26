package com.example.adoptmev5.ui.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.adoptmev5.AdminUsersListActivity;
import com.example.adoptmev5.LoginActivity;
import com.example.adoptmev5.R;
import com.example.adoptmev5.UserChatActivity;

public class MenuFragment extends Fragment {

    private LinearLayout menuCompatibilityTest, menuProfile, menuSettings, menuHelp, menuAbout, menuLogout;
    private TextView userName;
    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        prefs = requireActivity().getSharedPreferences("adoptme_prefs", requireContext().MODE_PRIVATE);

        initViews(view);
        setupUserInfo();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        userName = view.findViewById(R.id.user_name);
        menuCompatibilityTest = view.findViewById(R.id.menu_compatibility_test);
        menuProfile = view.findViewById(R.id.menu_profile);
        menuSettings = view.findViewById(R.id.menu_settings);
        menuHelp = view.findViewById(R.id.menu_help);
        menuAbout = view.findViewById(R.id.menu_about);
        menuLogout = view.findViewById(R.id.menu_logout);
    }

    private void setupUserInfo() {
        String nombres = prefs.getString("nombres", "");
        String apellidos = prefs.getString("apellidos", "");
        String fullName = nombres + " " + apellidos;

        if (userName != null) {
            userName.setText(fullName.trim().isEmpty() ? "Usuario AdoptMe" : fullName);
        }
    }

    private void setupListeners() {
        if (menuCompatibilityTest != null) {
            menuCompatibilityTest.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Test de Compatibilidad", Toast.LENGTH_SHORT).show()
            );
        }

        if (menuProfile != null) {
            menuProfile.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), EditProfileActivity.class);
                startActivity(intent);
            });
        }

        if (menuSettings != null) {
            menuSettings.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Configuración", Toast.LENGTH_SHORT).show()
            );
        }

        if (menuHelp != null) {
            menuHelp.setOnClickListener(v -> openChatBasedOnRole());
        }

        if (menuAbout != null) {
            menuAbout.setOnClickListener(v ->
                Toast.makeText(requireContext(), "Acerca de AdoptMe", Toast.LENGTH_SHORT).show()
            );
        }

        if (menuLogout != null) {
            menuLogout.setOnClickListener(v -> logout());
        }
    }

    private void openChatBasedOnRole() {
        String role = prefs.getString("role", "user");
        Intent intent;

        if (role.equals("admin")) {
            // Si es admin, abrir lista de usuarios
            intent = new Intent(requireContext(), AdminUsersListActivity.class);
        } else {
            // Si es user, abrir chat grupal con admins
            intent = new Intent(requireContext(), UserChatActivity.class);
        }

        startActivity(intent);
    }

    private void logout() {
        // Limpiar SharedPreferences
        prefs.edit().clear().apply();

        // Ir a LoginActivity
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}

