package com.example.adoptmev5.ui.menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adoptmev5.R;

public class UbicationActivity extends AppCompatActivity {

    private EditText editLocation;
    private Button btnSaveLocation, btnDetectLocation;
    private TextView tvCurrentLocation, tvRadiusValue;
    private SeekBar seekbarRadius;
    private CheckBox cbShareLocation;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ubication);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        editLocation = findViewById(R.id.edit_location);
        btnSaveLocation = findViewById(R.id.btn_save_location);
        btnDetectLocation = findViewById(R.id.btn_detect_location);
        tvCurrentLocation = findViewById(R.id.tv_current_location);
        tvRadiusValue = findViewById(R.id.tv_radius_value);
        seekbarRadius = findViewById(R.id.seekbar_radius);
        cbShareLocation = findViewById(R.id.cb_share_location);

        // SharedPreferences
        preferences = getSharedPreferences("UbicationPrefs", MODE_PRIVATE);

        // Cargar valores guardados
        loadPreferences();

        // Guardar ubicación manual
        btnSaveLocation.setOnClickListener(v -> {
            String location = editLocation.getText().toString().trim();
            if (!location.isEmpty()) {
                tvCurrentLocation.setText(location); // actualizar UI
                savePreference("current_location", location);
            }
        });

        // Detectar ubicación (simulada)
        btnDetectLocation.setOnClickListener(v -> {
            String detectedLocation = "Lima, Perú"; // Aquí iría tu lógica con GPS
            tvCurrentLocation.setText(detectedLocation);
            savePreference("current_location", detectedLocation);
        });

        // Cambiar radio de búsqueda
        seekbarRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String radiusText = progress + " km";
                tvRadiusValue.setText(radiusText);
                savePreference("radius", progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Compartir ubicación
        cbShareLocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            savePreference("share_location", isChecked);
        });
    }

    private void loadPreferences() {
        String location = preferences.getString("current_location", "Lima, Perú");
        int radius = preferences.getInt("radius", 20);
        boolean shareLocation = preferences.getBoolean("share_location", false);

        tvCurrentLocation.setText(location);
        editLocation.setText(location);
        seekbarRadius.setProgress(radius);
        tvRadiusValue.setText(radius + " km");
        cbShareLocation.setChecked(shareLocation);
    }

    private void savePreference(String key, Object value) {
        SharedPreferences.Editor editor = preferences.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        }
        editor.apply();
    }
}
