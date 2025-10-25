package com.example.adoptmev5.ui.menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adoptmev5.R;
import com.example.adoptmev5.ui.menu.AccessibilityHelper;

public class AccesibilityActivity extends AppCompatActivity {

    private CheckBox cbTextoGrande;
    private CheckBox chkContrasteAlto;
    private ImageView btnBack;
    private SharedPreferences sharedPreferences;

    public static final String PREFS_NAME = "accessibility_prefs";
    public static final String KEY_LARGE_TEXT = "large_text";
    public static final String KEY_HIGH_CONTRAST = "high_contrast";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_accesibility);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupSharedPreferences();
        loadSavedPreferences();
        setupListeners();

        // Aplicar configuraciones actuales a esta actividad
        AccessibilityHelper.applyAccessibilitySettings(this);
    }

    private void initializeViews() {
        cbTextoGrande = findViewById(R.id.cb_texto_grande);
        chkContrasteAlto = findViewById(R.id.chkContrasteAlto);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupSharedPreferences() {
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
    }

    private void loadSavedPreferences() {
        boolean largeText = sharedPreferences.getBoolean(KEY_LARGE_TEXT, false);
        boolean highContrast = sharedPreferences.getBoolean(KEY_HIGH_CONTRAST, false);

        cbTextoGrande.setChecked(largeText);
        chkContrasteAlto.setChecked(highContrast);
    }

    private void setupListeners() {
        // Listener para texto grande
        cbTextoGrande.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_LARGE_TEXT, isChecked);
            editor.apply();

            // Aplicar cambios inmediatamente
            AccessibilityHelper.applyAccessibilitySettings(this);
        });

        // Listener para contraste alto
        chkContrasteAlto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_HIGH_CONTRAST, isChecked);
            editor.apply();

            // Aplicar cambios inmediatamente
            AccessibilityHelper.applyAccessibilitySettings(this);
        });

        // Listener para botón de regreso
        btnBack.setOnClickListener(v -> finish());
    }
}