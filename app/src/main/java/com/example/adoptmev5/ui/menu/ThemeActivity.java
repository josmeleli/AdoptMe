package com.example.adoptmev5.ui.menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adoptmev5.R;

public class ThemeActivity extends AppCompatActivity {

    private RadioGroup radioGroupTema;
    private RadioButton radioClaro, radioOscuro, radioSistema;
    private ImageView btnBack;
    private SharedPreferences sharedPreferences;

    // Constantes para los temas
    public static final String PREFS_NAME = "theme_prefs";
    public static final String THEME_KEY = "selected_theme";
    public static final int THEME_LIGHT = 0;
    public static final int THEME_DARK = 1;
    public static final int THEME_SYSTEM = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 🔥 APLICAR TEMA ANTES DE SUPER.ONCREATE
        applyStoredTheme(this);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_theme);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupListeners();
        loadSavedTheme();
    }

    private void initViews() {
        radioGroupTema = findViewById(R.id.radioGroupTema);
        radioClaro = findViewById(R.id.radioClaro);
        radioOscuro = findViewById(R.id.radioOscuro);
        radioSistema = findViewById(R.id.radioSistema);
        btnBack = findViewById(R.id.btn_back);

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        radioGroupTema.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedTheme = THEME_LIGHT;

            if (checkedId == R.id.radioClaro) {
                selectedTheme = THEME_LIGHT;
            } else if (checkedId == R.id.radioOscuro) {
                selectedTheme = THEME_DARK;
            } else if (checkedId == R.id.radioSistema) {
                selectedTheme = THEME_SYSTEM;
            }

            // Guardar y aplicar inmediatamente
            saveTheme(selectedTheme);
            applyThemeChange(selectedTheme);
        });
    }

    private void loadSavedTheme() {
        int savedTheme = sharedPreferences.getInt(THEME_KEY, THEME_LIGHT);

        switch (savedTheme) {
            case THEME_LIGHT:
                radioClaro.setChecked(true);
                break;
            case THEME_DARK:
                radioOscuro.setChecked(true);
                break;
            case THEME_SYSTEM:
                radioSistema.setChecked(true);
                break;
        }
    }

    private void saveTheme(int theme) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(THEME_KEY, theme);
        editor.apply();
    }

    // ✅ Método para aplicar tema guardado desde cualquier Activity
    public static void applyStoredTheme(AppCompatActivity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedTheme = prefs.getInt(THEME_KEY, THEME_LIGHT);
        applyThemeChange(savedTheme);
    }

    // ✅ Método para aplicar tema en Application
    public static void applyStoredThemeGlobal(android.app.Application app) {
        SharedPreferences prefs = app.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedTheme = prefs.getInt(THEME_KEY, THEME_LIGHT);
        applyThemeChange(savedTheme);
    }

    private static void applyThemeChange(int theme) {
        switch (theme) {
            case THEME_LIGHT:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case THEME_SYSTEM:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }
}
