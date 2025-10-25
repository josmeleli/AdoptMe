package com.example.adoptmev5.ui.menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.adoptmev5.R;

public class NotifyActivity extends AppCompatActivity {

    private CheckBox cbPushNotifications, cbSounds, cbVibration;
    private CheckBox cbNewPets, cbAdoptionUpdates, cbMessages, cbReminders;

    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notify);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        cbPushNotifications = findViewById(R.id.cb_push_notifications);
        cbSounds = findViewById(R.id.cb_sounds);
        cbVibration = findViewById(R.id.cb_vibration);
        cbNewPets = findViewById(R.id.cb_new_pets);
        cbAdoptionUpdates = findViewById(R.id.cb_adoption_updates);
        cbMessages = findViewById(R.id.cb_messages);
        cbReminders = findViewById(R.id.cb_reminders);

        // Inicializar SharedPreferences
        preferences = getSharedPreferences("NotifyPrefs", MODE_PRIVATE);

        // Cargar estados guardados
        loadPreferences();

        // Guardar automáticamente cuando cambie un checkbox
        cbPushNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> savePreference("push_notifications", isChecked));
        cbSounds.setOnCheckedChangeListener((buttonView, isChecked) -> savePreference("sounds", isChecked));
        cbVibration.setOnCheckedChangeListener((buttonView, isChecked) -> savePreference("vibration", isChecked));
        cbNewPets.setOnCheckedChangeListener((buttonView, isChecked) -> savePreference("new_pets", isChecked));
        cbAdoptionUpdates.setOnCheckedChangeListener((buttonView, isChecked) -> savePreference("adoption_updates", isChecked));
        cbMessages.setOnCheckedChangeListener((buttonView, isChecked) -> savePreference("messages", isChecked));
        cbReminders.setOnCheckedChangeListener((buttonView, isChecked) -> savePreference("reminders", isChecked));
    }

    private void loadPreferences() {
        cbPushNotifications.setChecked(preferences.getBoolean("push_notifications", true));
        cbSounds.setChecked(preferences.getBoolean("sounds", true));
        cbVibration.setChecked(preferences.getBoolean("vibration", true));
        cbNewPets.setChecked(preferences.getBoolean("new_pets", true));
        cbAdoptionUpdates.setChecked(preferences.getBoolean("adoption_updates", true));
        cbMessages.setChecked(preferences.getBoolean("messages", true));
        cbReminders.setChecked(preferences.getBoolean("reminders", false));
    }

    private void savePreference(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
