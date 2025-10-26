package com.example.adoptmev5.ui.adoption;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.adoptmev5.R;
import com.example.adoptmev5.api.AdoptionApiService;
import com.example.adoptmev5.models.AdoptionRequest;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AdoptionFormActivity extends AppCompatActivity {

    private int currentStep = 1;
    private int userId;
    private int petId;
    private String petName;
    private AdoptionRequest adoptionRequest;

    // UI Components
    private TextView tvTitle, tvStepIndicator;
    private ProgressBar progressBar;
    private LinearLayout step1Layout, step2Layout, step3Layout, step4Layout;
    private Button btnNext, btnPrevious, btnSubmit;

    // Step 1: Información Personal
    private EditText etNombresCompletos, etEmail, etTelefono, etFechaNacimiento;
    private EditText etDireccionCompleta, etCiudad, etDistrito;

    // Step 2: Información del Hogar
    private Spinner spinnerTipoVivienda, spinnerPropietarioAcepta;
    private EditText etMiembrosFamilia;
    private RadioGroup rgHayNinos, rgAlergiasFamilia;

    // Step 3: Experiencia con Mascotas
    private RadioGroup rgTieneOtrasMascotas;
    private EditText etDescripcionOtrasMascotas, etExperienciaPrevia, etTiempoSolaMascota;
    private RadioGroup rgTieneVeterinario;
    private EditText etPresupuestoMensual;

    // Step 4: Motivación y Compromiso
    private EditText etMotivacionAdopcion, etConocimientoRaza, etCompromisoLargoPlazo;
    private RadioGroup rgDispuestoEntrenar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adoption_form);

        // Obtener datos del intent
        userId = getIntent().getIntExtra("user_id", 0);
        petId = getIntent().getIntExtra("pet_id", 0);
        petName = getIntent().getStringExtra("pet_name");

        adoptionRequest = new AdoptionRequest();
        adoptionRequest.setUserId(userId);
        adoptionRequest.setPetId(petId);

        // Prellenar datos del usuario desde SharedPreferences
        SharedPreferences prefs = getSharedPreferences("adoptme_prefs", MODE_PRIVATE);
        String userNombres = prefs.getString("nombres", "");
        String userApellidos = prefs.getString("apellidos", "");
        String userEmail = prefs.getString("email", "");
        String userTelefono = prefs.getString("telefono", "");

        initializeViews();
        setupSpinners();
        setupStepNavigation();

        // Prellenar campos
        etNombresCompletos.setText(userNombres + " " + userApellidos);
        etEmail.setText(userEmail);
        etTelefono.setText(userTelefono);

        // Configurar selector de fecha
        etFechaNacimiento.setOnClickListener(v -> showDatePicker());

        showStep(1);
    }

    private void initializeViews() {
        tvTitle = findViewById(R.id.tv_form_title);
        tvStepIndicator = findViewById(R.id.tv_step_indicator);
        progressBar = findViewById(R.id.progress_bar);

        step1Layout = findViewById(R.id.step1_layout);
        step2Layout = findViewById(R.id.step2_layout);
        step3Layout = findViewById(R.id.step3_layout);
        step4Layout = findViewById(R.id.step4_layout);

        btnNext = findViewById(R.id.btn_next);
        btnPrevious = findViewById(R.id.btn_previous);
        btnSubmit = findViewById(R.id.btn_submit);

        // Step 1
        etNombresCompletos = findViewById(R.id.et_nombres_completos);
        etEmail = findViewById(R.id.et_email);
        etTelefono = findViewById(R.id.et_telefono);
        etFechaNacimiento = findViewById(R.id.et_fecha_nacimiento);
        etDireccionCompleta = findViewById(R.id.et_direccion_completa);
        etCiudad = findViewById(R.id.et_ciudad);
        etDistrito = findViewById(R.id.et_distrito);

        // Step 2
        spinnerTipoVivienda = findViewById(R.id.spinner_tipo_vivienda);
        spinnerPropietarioAcepta = findViewById(R.id.spinner_propietario_acepta);
        etMiembrosFamilia = findViewById(R.id.et_miembros_familia);
        rgHayNinos = findViewById(R.id.rg_hay_ninos);
        rgAlergiasFamilia = findViewById(R.id.rg_alergias_familia);

        // Step 3
        rgTieneOtrasMascotas = findViewById(R.id.rg_tiene_otras_mascotas);
        etDescripcionOtrasMascotas = findViewById(R.id.et_descripcion_otras_mascotas);
        etExperienciaPrevia = findViewById(R.id.et_experiencia_previa);
        etTiempoSolaMascota = findViewById(R.id.et_tiempo_sola_mascota);
        rgTieneVeterinario = findViewById(R.id.rg_tiene_veterinario);
        etPresupuestoMensual = findViewById(R.id.et_presupuesto_mensual);

        // Step 4
        etMotivacionAdopcion = findViewById(R.id.et_motivacion_adopcion);
        etConocimientoRaza = findViewById(R.id.et_conocimiento_raza);
        etCompromisoLargoPlazo = findViewById(R.id.et_compromiso_largo_plazo);
        rgDispuestoEntrenar = findViewById(R.id.rg_dispuesto_entrenar);
    }

    private void setupSpinners() {
        // Spinner Tipo de Vivienda
        String[] tiposVivienda = {"Casa", "Departamento", "Casa con jardín"};
        ArrayAdapter<String> adapterVivienda = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tiposVivienda);
        adapterVivienda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoVivienda.setAdapter(adapterVivienda);

        // Spinner Propietario Acepta
        String[] propietarioAcepta = {"Sí", "No", "Soy propietario"};
        ArrayAdapter<String> adapterPropietario = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, propietarioAcepta);
        adapterPropietario.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPropietarioAcepta.setAdapter(adapterPropietario);
    }

    private void setupStepNavigation() {
        btnNext.setOnClickListener(v -> {
            if (validateCurrentStep()) {
                saveCurrentStep();
                if (currentStep < 4) {
                    showStep(currentStep + 1);
                }
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (currentStep > 1) {
                saveCurrentStep();
                showStep(currentStep - 1);
            }
        });

        btnSubmit.setOnClickListener(v -> {
            if (validateCurrentStep()) {
                saveCurrentStep();
                submitForm();
            }
        });
    }

    private void showStep(int step) {
        currentStep = step;

        // Ocultar todos los pasos
        step1Layout.setVisibility(View.GONE);
        step2Layout.setVisibility(View.GONE);
        step3Layout.setVisibility(View.GONE);
        step4Layout.setVisibility(View.GONE);

        // Mostrar paso actual
        switch (step) {
            case 1:
                step1Layout.setVisibility(View.VISIBLE);
                tvTitle.setText("Información Personal");
                tvStepIndicator.setText("Paso 1 de 4");
                progressBar.setProgress(25);
                btnPrevious.setVisibility(View.GONE);
                btnNext.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.GONE);
                break;
            case 2:
                step2Layout.setVisibility(View.VISIBLE);
                tvTitle.setText("Información del Hogar");
                tvStepIndicator.setText("Paso 2 de 4");
                progressBar.setProgress(50);
                btnPrevious.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.GONE);
                break;
            case 3:
                step3Layout.setVisibility(View.VISIBLE);
                tvTitle.setText("Experiencia con Mascotas");
                tvStepIndicator.setText("Paso 3 de 4");
                progressBar.setProgress(75);
                btnPrevious.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.GONE);
                break;
            case 4:
                step4Layout.setVisibility(View.VISIBLE);
                tvTitle.setText("Motivación y Compromiso");
                tvStepIndicator.setText("Paso 4 de 4");
                progressBar.setProgress(100);
                btnPrevious.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.VISIBLE);
                break;
        }
    }

    private boolean validateCurrentStep() {
        switch (currentStep) {
            case 1:
                if (etNombresCompletos.getText().toString().trim().isEmpty()) {
                    etNombresCompletos.setError("Campo requerido");
                    return false;
                }
                if (etEmail.getText().toString().trim().isEmpty()) {
                    etEmail.setError("Campo requerido");
                    return false;
                }
                if (etTelefono.getText().toString().trim().isEmpty()) {
                    etTelefono.setError("Campo requerido");
                    return false;
                }
                if (etFechaNacimiento.getText().toString().trim().isEmpty()) {
                    etFechaNacimiento.setError("Campo requerido");
                    return false;
                }
                if (etDireccionCompleta.getText().toString().trim().isEmpty()) {
                    etDireccionCompleta.setError("Campo requerido");
                    return false;
                }
                if (etCiudad.getText().toString().trim().isEmpty()) {
                    etCiudad.setError("Campo requerido");
                    return false;
                }
                if (etDistrito.getText().toString().trim().isEmpty()) {
                    etDistrito.setError("Campo requerido");
                    return false;
                }
                break;
            case 2:
                if (etMiembrosFamilia.getText().toString().trim().isEmpty()) {
                    etMiembrosFamilia.setError("Campo requerido");
                    return false;
                }
                if (rgHayNinos.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(this, "Por favor indica si hay niños en la familia", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (rgAlergiasFamilia.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(this, "Por favor indica si hay alergias en la familia", Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
            case 3:
                if (rgTieneOtrasMascotas.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(this, "Por favor indica si tienes otras mascotas", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (etExperienciaPrevia.getText().toString().trim().isEmpty()) {
                    etExperienciaPrevia.setError("Campo requerido");
                    return false;
                }
                if (etTiempoSolaMascota.getText().toString().trim().isEmpty()) {
                    etTiempoSolaMascota.setError("Campo requerido");
                    return false;
                }
                if (rgTieneVeterinario.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(this, "Por favor indica si tienes veterinario", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (etPresupuestoMensual.getText().toString().trim().isEmpty()) {
                    etPresupuestoMensual.setError("Campo requerido");
                    return false;
                }
                break;
            case 4:
                if (etMotivacionAdopcion.getText().toString().trim().isEmpty()) {
                    etMotivacionAdopcion.setError("Campo requerido");
                    return false;
                }
                if (etConocimientoRaza.getText().toString().trim().isEmpty()) {
                    etConocimientoRaza.setError("Campo requerido");
                    return false;
                }
                if (rgDispuestoEntrenar.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(this, "Por favor indica si estás dispuesto a entrenar", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (etCompromisoLargoPlazo.getText().toString().trim().isEmpty()) {
                    etCompromisoLargoPlazo.setError("Campo requerido");
                    return false;
                }
                break;
        }
        return true;
    }

    private void saveCurrentStep() {
        switch (currentStep) {
            case 1:
                adoptionRequest.setNombresCompletos(etNombresCompletos.getText().toString().trim());
                adoptionRequest.setEmail(etEmail.getText().toString().trim());
                adoptionRequest.setTelefono(etTelefono.getText().toString().trim());
                adoptionRequest.setFechaNacimiento(etFechaNacimiento.getText().toString().trim());
                adoptionRequest.setDireccionCompleta(etDireccionCompleta.getText().toString().trim());
                adoptionRequest.setCiudad(etCiudad.getText().toString().trim());
                adoptionRequest.setDistrito(etDistrito.getText().toString().trim());
                break;
            case 2:
                adoptionRequest.setTipoVivienda(spinnerTipoVivienda.getSelectedItem().toString());
                adoptionRequest.setPropietarioAceptaMascotas(spinnerPropietarioAcepta.getSelectedItem().toString());
                adoptionRequest.setMiembrosFamilia(Integer.parseInt(etMiembrosFamilia.getText().toString().trim()));
                adoptionRequest.setHayNinos(getRadioButtonText(rgHayNinos));
                adoptionRequest.setAlergiasFamilia(getRadioButtonText(rgAlergiasFamilia));
                break;
            case 3:
                adoptionRequest.setTieneOtrasMascotas(getRadioButtonText(rgTieneOtrasMascotas));
                String descripcionOtras = etDescripcionOtrasMascotas.getText().toString().trim();
                adoptionRequest.setDescripcionOtrasMascotas(descripcionOtras.isEmpty() ? null : descripcionOtras);
                adoptionRequest.setExperienciaPrevia(etExperienciaPrevia.getText().toString().trim());
                adoptionRequest.setTiempoSolaMascota(etTiempoSolaMascota.getText().toString().trim());
                adoptionRequest.setTieneVeterinario(getRadioButtonText(rgTieneVeterinario));
                adoptionRequest.setPresupuestoMensual(etPresupuestoMensual.getText().toString().trim());
                break;
            case 4:
                adoptionRequest.setMotivacionAdopcion(etMotivacionAdopcion.getText().toString().trim());
                adoptionRequest.setConocimientoRaza(etConocimientoRaza.getText().toString().trim());
                adoptionRequest.setDispuestoEntrenar(getRadioButtonText(rgDispuestoEntrenar));
                adoptionRequest.setCompromisoLargoPlazo(etCompromisoLargoPlazo.getText().toString().trim());
                break;
        }
    }

    private String getRadioButtonText(RadioGroup radioGroup) {
        int selectedId = radioGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton radioButton = findViewById(selectedId);
            return radioButton.getText().toString();
        }
        return "";
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    etFechaNacimiento.setText(sdf.format(selectedDate.getTime()));
                }, year, month, day);

        // Establecer fecha máxima (hoy - 18 años)
        calendar.add(Calendar.YEAR, -18);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    private void submitForm() {
        try {
            JSONObject requestData = new JSONObject();
            requestData.put("user_id", adoptionRequest.getUserId());
            requestData.put("pet_id", adoptionRequest.getPetId());
            requestData.put("nombres_completos", adoptionRequest.getNombresCompletos());
            requestData.put("email", adoptionRequest.getEmail());
            requestData.put("telefono", adoptionRequest.getTelefono());
            requestData.put("fecha_nacimiento", adoptionRequest.getFechaNacimiento());
            requestData.put("direccion_completa", adoptionRequest.getDireccionCompleta());
            requestData.put("ciudad", adoptionRequest.getCiudad());
            requestData.put("distrito", adoptionRequest.getDistrito());
            requestData.put("tipo_vivienda", adoptionRequest.getTipoVivienda());
            requestData.put("propietario_acepta_mascotas", adoptionRequest.getPropietarioAceptaMascotas());
            requestData.put("miembros_familia", adoptionRequest.getMiembrosFamilia());
            requestData.put("hay_ninos", adoptionRequest.getHayNinos());
            requestData.put("alergias_familia", adoptionRequest.getAlergiasFamilia());
            requestData.put("tiene_otras_mascotas", adoptionRequest.getTieneOtrasMascotas());
            requestData.put("descripcion_otras_mascotas", adoptionRequest.getDescripcionOtrasMascotas());
            requestData.put("experiencia_previa", adoptionRequest.getExperienciaPrevia());
            requestData.put("tiempo_sola_mascota", adoptionRequest.getTiempoSolaMascota());
            requestData.put("tiene_veterinario", adoptionRequest.getTieneVeterinario());
            requestData.put("presupuesto_mensual", adoptionRequest.getPresupuestoMensual());
            requestData.put("motivacion_adopcion", adoptionRequest.getMotivacionAdopcion());
            requestData.put("conocimiento_raza", adoptionRequest.getConocimientoRaza());
            requestData.put("dispuesto_entrenar", adoptionRequest.getDispuestoEntrenar());
            requestData.put("compromiso_largo_plazo", adoptionRequest.getCompromisoLargoPlazo());

            // Log del JSON para debug
            android.util.Log.d("AdoptionForm", "Enviando solicitud: " + requestData.toString());

            // Deshabilitar botón mientras se envía
            btnSubmit.setEnabled(false);
            btnSubmit.setText("Enviando...");

            AdoptionApiService.createRequest(requestData, new AdoptionApiService.CreateRequestCallback() {
                @Override
                public void onSuccess(int requestId, String message) {
                    android.util.Log.d("AdoptionForm", "Solicitud creada: ID=" + requestId);
                    runOnUiThread(() -> {
                        Toast.makeText(AdoptionFormActivity.this, message, Toast.LENGTH_LONG).show();
                        // Volver a la pantalla principal
                        finish();
                    });
                }

                @Override
                public void onError(String error) {
                    android.util.Log.e("AdoptionForm", "Error al crear solicitud: " + error);
                    runOnUiThread(() -> {
                        Toast.makeText(AdoptionFormActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("Enviar Solicitud");
                    });
                }
            });

        } catch (Exception e) {
            android.util.Log.e("AdoptionForm", "Exception al preparar solicitud", e);
            Toast.makeText(this, "Error al preparar la solicitud: " + e.getMessage(), Toast.LENGTH_LONG).show();
            btnSubmit.setEnabled(true);
            btnSubmit.setText("Enviar Solicitud");
        }
    }
}

