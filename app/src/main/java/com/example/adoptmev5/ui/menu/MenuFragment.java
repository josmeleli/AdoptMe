package com.example.adoptmev5.ui.menu;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.adoptmev5.LoginActivity;
import com.example.adoptmev5.databinding.FragmentMenuBinding;

public class MenuFragment extends Fragment {

    private FragmentMenuBinding binding;
    private boolean isPreferencesExpanded = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MenuViewModel menuViewModel =
                new ViewModelProvider(this).get(MenuViewModel.class);

        binding = FragmentMenuBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        setupClickListeners();

        return root;
    }

    private void setupClickListeners() {
        // Click listener para expandir/contraer el menú de preferencias
        binding.menuPreferencesHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePreferencesMenu();
            }
        });

        // Click listeners para cada opción del submenu
        binding.submenuTema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la pantalla de Tema de la Aplicación
                navigateToThemeSettings();
            }
        });

        binding.submenuNotificaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la pantalla de Notificaciones
                navigateToNotificationSettings();
            }
        });

        binding.submenuFiltros.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la pantalla de Filtros de Mascotas
                navigateToFiltersSettings();
            }
        });

        binding.submenuUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la pantalla de Ubicación
                navigateToLocationSettings();
            }
        });

        binding.submenuAccesibilidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la pantalla de Accesibilidad
                navigateToAccessibilitySettings();
            }
        });

        // Otros click listeners para las opciones principales del menú
        binding.menuCompatibilityTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a Test de Compatibilidad
                navigateToCompatibilityTest();
            }
        });

        binding.menuProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a Mi Perfil
                navigateToProfile();
            }
        });

        binding.menuSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a Configuración
                navigateToSettings();
            }
        });

        binding.menuHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a Ayuda y Soporte
                navigateToHelp();
            }
        });

        binding.menuAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a Acerca de
                navigateToAbout();
            }
        });

        binding.menuLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cerrar sesión
                logout();
            }
        });
    }

    private void togglePreferencesMenu() {
        if (isPreferencesExpanded) {
            // Contraer el menú
            collapsePreferencesMenu();
        } else {
            // Expandir el menú
            expandPreferencesMenu();
        }
        isPreferencesExpanded = !isPreferencesExpanded;
    }

    private void expandPreferencesMenu() {
        // Mostrar el submenu con animación
        binding.preferencesSubmenu.setVisibility(View.VISIBLE);

        // Rotar la flecha hacia arriba
        binding.preferencesArrow.animate()
                .rotation(180f)
                .setDuration(300)
                .start();

        // Animación de expansión suave
        binding.preferencesSubmenu.setAlpha(0f);
        binding.preferencesSubmenu.setScaleY(0.8f);
        binding.preferencesSubmenu.animate()
                .alpha(1f)
                .scaleY(1f)
                .setDuration(300)
                .start();
    }

    private void collapsePreferencesMenu() {
        // Rotar la flecha hacia abajo
        binding.preferencesArrow.animate()
                .rotation(0f)
                .setDuration(300)
                .start();

        // Animación de contracción con callback para ocultar
        binding.preferencesSubmenu.animate()
                .alpha(0f)
                .scaleY(0.8f)
                .setDuration(300)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        binding.preferencesSubmenu.setVisibility(View.GONE);
                    }
                })
                .start();
    }

    // Métodos de navegación - implementa según tu arquitectura de navegación
    private void navigateToThemeSettings() {
        Intent intent = new Intent(getActivity(), ThemeActivity.class);
        startActivity(intent);
    }

    private void navigateToNotificationSettings() {
        Intent intent = new Intent(getActivity(), NotifyActivity.class);
        startActivity(intent);
    }

    private void navigateToFiltersSettings() {
        Intent intent = new Intent(getActivity(), FilterActivity.class);
        startActivity(intent);
    }

    private void navigateToLocationSettings() {
        Intent intent = new Intent(getActivity(), UbicationActivity.class);
        startActivity(intent);
    }

    private void navigateToAccessibilitySettings() {
        Intent intent = new Intent(getActivity(), AccesibilityActivity.class);
        startActivity(intent);
    }

    private void navigateToCompatibilityTest() {
        Intent intent = new Intent(getActivity(), CompatibilityActivity.class);
        startActivity(intent);
    }

    private void navigateToProfile() {
        // Abrir directamente la edición de perfil
        Intent intent = new Intent(getActivity(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void navigateToSettings() {
        Intent intent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(intent);
    }

    private void navigateToHelp() {
        Intent intent = new Intent(getActivity(), HelpActivity.class);
        startActivity(intent);
    }

    private void navigateToAbout() {
        Intent intent = new Intent(getActivity(), AboutActivity.class);
        startActivity(intent);
    }

    private void logout() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
    // Método temporal para mostrar mensajes (puedes usar Toast o Snackbar)
    private void showTemporaryMessage(String message) {
        // Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        System.out.println(message); // Por ahora solo imprime en consola
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}