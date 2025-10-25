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

public class FilterActivity extends AppCompatActivity {

    // Nombre del archivo de preferencias
    private static final String PREFS_NAME = "FilterPreferences";

    // Variables para los checkboxes
    private CheckBox cbPerro, cbGato, cbConejo, cbAve, cbReptil;
    private CheckBox cbCachorro, cbJoven, cbAdulto, cbSenior;
    private CheckBox cbPequeno, cbMediano, cbGrande, cbGigante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_filter);

        // Ajuste para Edge-to-Edge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar los checkboxes
        cbPerro = findViewById(R.id.cb_perro);
        cbGato = findViewById(R.id.cb_gato);
        cbConejo = findViewById(R.id.cb_conejo);
        cbAve = findViewById(R.id.cb_ave);
        cbReptil = findViewById(R.id.cb_reptil);

        cbCachorro = findViewById(R.id.cb_cachorro);
        cbJoven = findViewById(R.id.cb_joven);
        cbAdulto = findViewById(R.id.cb_adulto);
        cbSenior = findViewById(R.id.cb_senior);

        cbPequeno = findViewById(R.id.cb_pequeno);
        cbMediano = findViewById(R.id.cb_mediano);
        cbGrande = findViewById(R.id.cb_grande);
        cbGigante = findViewById(R.id.cb_gigante);

        // Cargar el estado de los checkboxes
        loadPreferences();

        // Guardar cambios al tocar cada checkbox
        setCheckBoxListener(cbPerro, "perro");
        setCheckBoxListener(cbGato, "gato");
        setCheckBoxListener(cbConejo, "conejo");
        setCheckBoxListener(cbAve, "ave");
        setCheckBoxListener(cbReptil, "reptil");

        setCheckBoxListener(cbCachorro, "cachorro");
        setCheckBoxListener(cbJoven, "joven");
        setCheckBoxListener(cbAdulto, "adulto");
        setCheckBoxListener(cbSenior, "senior");

        setCheckBoxListener(cbPequeno, "pequeno");
        setCheckBoxListener(cbMediano, "mediano");
        setCheckBoxListener(cbGrande, "grande");
        setCheckBoxListener(cbGigante, "gigante");
    }

    // Método para asignar el listener y guardar cambios
    private void setCheckBoxListener(CheckBox checkBox, String key) {
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(key, isChecked);
            editor.apply();
        });
    }

    // Método para cargar las preferencias guardadas
    private void loadPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        cbPerro.setChecked(prefs.getBoolean("perro", true));
        cbGato.setChecked(prefs.getBoolean("gato", true));
        cbConejo.setChecked(prefs.getBoolean("conejo", false));
        cbAve.setChecked(prefs.getBoolean("ave", false));
        cbReptil.setChecked(prefs.getBoolean("reptil", false));

        cbCachorro.setChecked(prefs.getBoolean("cachorro", true));
        cbJoven.setChecked(prefs.getBoolean("joven", true));
        cbAdulto.setChecked(prefs.getBoolean("adulto", true));
        cbSenior.setChecked(prefs.getBoolean("senior", false));

        cbPequeno.setChecked(prefs.getBoolean("pequeno", true));
        cbMediano.setChecked(prefs.getBoolean("mediano", true));
        cbGrande.setChecked(prefs.getBoolean("grande", false));
        cbGigante.setChecked(prefs.getBoolean("gigante", false));
    }
}
