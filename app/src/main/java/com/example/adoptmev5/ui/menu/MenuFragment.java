package com.example.adoptmev5.ui.menu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.adoptmev5.AdminUsersListActivity;
import com.example.adoptmev5.R;
import com.example.adoptmev5.UserChatActivity;

public class MenuFragment extends Fragment {

    private LinearLayout menuHelp;
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
        menuHelp = view.findViewById(R.id.menu_help);
        userName = view.findViewById(R.id.user_name);
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
        if (menuHelp != null) {
            menuHelp.setOnClickListener(v -> openChatBasedOnRole());
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
}

