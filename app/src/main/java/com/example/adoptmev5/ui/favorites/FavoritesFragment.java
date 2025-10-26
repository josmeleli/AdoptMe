package com.example.adoptmev5.ui.favorites;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptmev5.R;
import com.example.adoptmev5.api.AdoptionApiService;
import com.example.adoptmev5.models.AdoptionRequestResponse;

import org.json.JSONObject;

import java.util.List;

public class FavoritesFragment extends Fragment {

    private static final String TAG = "FavoritesFragment";
    private RecyclerView recyclerRequests;
    private AdoptionRequestsAdapter adapter;
    private ProgressBar progressLoading;
    private View emptyState;

    // Estadísticas
    private TextView tvTotal, tvPendiente, tvAprobada, tvRechazada;

    // Botones de filtro
    private Button btnFilterAll, btnFilterPendiente, btnFilterEnRevision, btnFilterAprobada, btnFilterRechazada;

    private int userId;
    private String currentFilter = null; // null = todas

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_favorites, container, false);

        // Obtener userId de SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("adoptme_prefs", requireContext().MODE_PRIVATE);
        userId = prefs.getInt("user_id", 0);

        // Inicializar vistas
        recyclerRequests = root.findViewById(R.id.recycler_requests);
        progressLoading = root.findViewById(R.id.progress_loading);
        emptyState = root.findViewById(R.id.empty_state);

        // Estadísticas
        tvTotal = root.findViewById(R.id.tv_total);
        tvPendiente = root.findViewById(R.id.tv_pendiente);
        tvAprobada = root.findViewById(R.id.tv_aprobada);
        tvRechazada = root.findViewById(R.id.tv_rechazada);

        // Botones de filtro
        btnFilterAll = root.findViewById(R.id.btn_filter_all);
        btnFilterPendiente = root.findViewById(R.id.btn_filter_pendiente);
        btnFilterEnRevision = root.findViewById(R.id.btn_filter_en_revision);
        btnFilterAprobada = root.findViewById(R.id.btn_filter_aprobada);
        btnFilterRechazada = root.findViewById(R.id.btn_filter_rechazada);

        // Configurar RecyclerView
        recyclerRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdoptionRequestsAdapter(this::onRequestClick);
        recyclerRequests.setAdapter(adapter);

        // Configurar filtros
        setupFilterButtons();

        // Cargar solicitudes
        if (userId != 0) {
            loadRequests();
        } else {
            Toast.makeText(getContext(), "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
        }

        return root;
    }

    private void setupFilterButtons() {
        btnFilterAll.setOnClickListener(v -> {
            currentFilter = null;
            updateFilterButtonsUI(btnFilterAll);
            loadRequests();
        });

        btnFilterPendiente.setOnClickListener(v -> {
            currentFilter = "pendiente";
            updateFilterButtonsUI(btnFilterPendiente);
            loadRequests();
        });

        btnFilterEnRevision.setOnClickListener(v -> {
            currentFilter = "en_revision";
            updateFilterButtonsUI(btnFilterEnRevision);
            loadRequests();
        });

        btnFilterAprobada.setOnClickListener(v -> {
            currentFilter = "aprobada";
            updateFilterButtonsUI(btnFilterAprobada);
            loadRequests();
        });

        btnFilterRechazada.setOnClickListener(v -> {
            currentFilter = "rechazada";
            updateFilterButtonsUI(btnFilterRechazada);
            loadRequests();
        });
    }

    private void updateFilterButtonsUI(Button activeButton) {
        // Resetear todos los botones
        resetButtonStyle(btnFilterAll);
        resetButtonStyle(btnFilterPendiente);
        resetButtonStyle(btnFilterEnRevision);
        resetButtonStyle(btnFilterAprobada);
        resetButtonStyle(btnFilterRechazada);

        // Activar el botón seleccionado
        activeButton.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_orange_dark));
        activeButton.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void resetButtonStyle(Button button) {
        button.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        button.setTextColor(getResources().getColor(android.R.color.black));
    }

    private void loadRequests() {
        Log.d(TAG, "loadRequests() - userId: " + userId + ", currentFilter: " + currentFilter);

        progressLoading.setVisibility(View.VISIBLE);
        recyclerRequests.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);

        AdoptionApiService.getMyRequests(userId, currentFilter, new AdoptionApiService.GetMyRequestsCallback() {
            @Override
            public void onSuccess(List<AdoptionRequestResponse> requests, JSONObject stats) {
                Log.d(TAG, "onSuccess - requests: " + (requests != null ? requests.size() : "null"));
                Log.d(TAG, "onSuccess - stats: " + (stats != null ? stats.toString() : "null"));

                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressLoading.setVisibility(View.GONE);

                    // Actualizar estadísticas (aunque esté vacío, muestra 0)
                    if (stats != null) {
                        tvTotal.setText(String.valueOf(stats.optInt("total", 0)));
                        tvPendiente.setText(String.valueOf(stats.optInt("pendiente", 0)));
                        tvAprobada.setText(String.valueOf(stats.optInt("aprobada", 0)));
                        tvRechazada.setText(String.valueOf(stats.optInt("rechazada", 0)));
                    } else {
                        // Si no hay stats, poner todos en 0
                        tvTotal.setText("0");
                        tvPendiente.setText("0");
                        tvAprobada.setText("0");
                        tvRechazada.setText("0");
                    }

                    if (requests == null || requests.isEmpty()) {
                        recyclerRequests.setVisibility(View.GONE);
                        emptyState.setVisibility(View.VISIBLE);
                        Log.d(TAG, "Mostrando estado vacío - No hay solicitudes");
                    } else {
                        recyclerRequests.setVisibility(View.VISIBLE);
                        emptyState.setVisibility(View.GONE);
                        adapter.setRequests(requests);
                        Log.d(TAG, "Mostrando " + requests.size() + " solicitudes");
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "onError: " + error);

                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressLoading.setVisibility(View.GONE);
                    recyclerRequests.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);

                    // Resetear estadísticas a 0 en caso de error
                    tvTotal.setText("0");
                    tvPendiente.setText("0");
                    tvAprobada.setText("0");
                    tvRechazada.setText("0");

                    // Mostrar error detallado
                    if (error.contains("500")) {
                        Toast.makeText(getContext(),
                            "Error del servidor. Por favor verifica tu backend PHP.\n" + error,
                            Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void onRequestClick(AdoptionRequestResponse request) {
        // TODO: Abrir detalles de la solicitud
        Toast.makeText(getContext(), "Solicitud #" + request.getId() + " - " + request.getStatusText(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargar solicitudes al volver
        if (userId != 0) {
            loadRequests();
        }
    }
}