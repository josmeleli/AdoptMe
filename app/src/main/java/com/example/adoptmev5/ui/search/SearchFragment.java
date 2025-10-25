package com.example.adoptmev5.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptmev5.R;
import com.example.adoptmev5.databinding.FragmentSearchBinding;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;

    // Variables para el RecyclerView y la API
    private RecyclerView recyclerPets;
    private PetsAdapter petsAdapter;
    private ProgressBar progressLoading;
    private View emptyState;
    private TextView tvResultsCount;
    private EditText searchInput;

    private String currentEspecieFilter = null; // null = todas
    private String currentSearchQuery = null; // Para búsqueda por texto
    private int currentPage = 1;
    private final int ITEMS_PER_PAGE = 6;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // Usar el nuevo layout dinámico
        View root = inflater.inflate(R.layout.fragment_search_dynamic, container, false);

        // Inicializar vistas
        recyclerPets = root.findViewById(R.id.recycler_pets);
        progressLoading = root.findViewById(R.id.progress_loading);
        emptyState = root.findViewById(R.id.empty_state);
        tvResultsCount = root.findViewById(R.id.tv_results_count);
        searchInput = root.findViewById(R.id.search_input);

        // Configurar RecyclerView con Grid de 2 columnas
        androidx.recyclerview.widget.GridLayoutManager layoutManager =
            new androidx.recyclerview.widget.GridLayoutManager(getContext(), 2);
        recyclerPets.setLayoutManager(layoutManager);

        // Inicializar adapter
        petsAdapter = new PetsAdapter(this::onPetClick);
        recyclerPets.setAdapter(petsAdapter);

        // Configurar campo de búsqueda
        setupSearchInput();

        // Configurar botones de categorías
        setupCategoryButtons(root);

        // Cargar mascotas desde la API
        loadPetsFromApi();

        return root;
    }

    /**
     * Configurar campo de búsqueda con filtrado al presionar Enter
     */
    private void setupSearchInput() {
        // Filtrar al presionar Enter/Search
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH) {
                String query = searchInput.getText().toString().trim().toLowerCase();

                if (query.isEmpty()) {
                    // Si está vacío, limpiar búsqueda
                    currentSearchQuery = null;
                    currentEspecieFilter = null;
                    currentPage = 1;
                    loadPetsFromApi();
                } else {
                    // Filtrar por query
                    currentSearchQuery = query;
                    currentPage = 1;
                    filterBySearchQuery(query);
                }

                // Ocultar teclado
                android.view.inputmethod.InputMethodManager imm =
                    (android.view.inputmethod.InputMethodManager) requireContext().getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
                }

                return true;
            }
            return false;
        });
    }

    /**
     * Filtrar mascotas según la búsqueda BÁSICA
     * Soporta: especie, raza, edad (número), sexo
     */
    private void filterBySearchQuery(String query) {
        // Limpiar filtros previos
        currentEspecieFilter = null;
        String edadFilter = null;
        String sexoFilter = null;
        String razaFilter = null;

        // 1. DETECTAR SI ES UN NÚMERO (EDAD EXACTA)
        if (query.matches("\\d+")) {
            edadFilter = query;
            android.util.Log.d("SearchFragment", "🔍 Filtrando por edad exacta: " + edadFilter);
        }
        // 2. DETECTAR SEXO
        else if (query.contains("macho")) {
            sexoFilter = "Macho";
            android.util.Log.d("SearchFragment", "🔍 Filtrando por sexo: Macho");
        } else if (query.contains("hembra")) {
            sexoFilter = "Hembra";
            android.util.Log.d("SearchFragment", "🔍 Filtrando por sexo: Hembra");
        }
        // 3. DETECTAR RAZAS COMUNES
        else if (query.contains("labrador")) {
            razaFilter = "Labrador";
            android.util.Log.d("SearchFragment", "🔍 Filtrando por raza: Labrador");
        } else if (query.contains("golden")) {
            razaFilter = "Golden";
            android.util.Log.d("SearchFragment", "🔍 Filtrando por raza: Golden");
        } else if (query.contains("bulldog")) {
            razaFilter = "Bulldog";
            android.util.Log.d("SearchFragment", "🔍 Filtrando por raza: Bulldog");
        } else if (query.contains("beagle")) {
            razaFilter = "Beagle";
            android.util.Log.d("SearchFragment", "🔍 Filtrando por raza: Beagle");
        } else if (query.contains("pastor")) {
            razaFilter = "Pastor";
            android.util.Log.d("SearchFragment", "🔍 Filtrando por raza: Pastor");
        } else if (query.contains("husky")) {
            razaFilter = "Husky";
            android.util.Log.d("SearchFragment", "🔍 Filtrando por raza: Husky");
        } else if (query.contains("siames") || query.contains("siamés")) {
            razaFilter = "Siamés";
            android.util.Log.d("SearchFragment", "🔍 Filtrando por raza: Siamés");
        } else if (query.contains("persa")) {
            razaFilter = "Persa";
            android.util.Log.d("SearchFragment", "🔍 Filtrando por raza: Persa");
        } else if (query.contains("mestizo")) {
            razaFilter = "Mestizo";
            android.util.Log.d("SearchFragment", "🔍 Filtrando por raza: Mestizo");
        }
        // 4. DETECTAR ESPECIE (última prioridad)
        else if (query.contains("perro")) {
            currentEspecieFilter = "Perro";
            android.util.Log.d("SearchFragment", "🔍 Filtrando por especie: Perro");
        } else if (query.contains("gato")) {
            currentEspecieFilter = "Gato";
            android.util.Log.d("SearchFragment", "🔍 Filtrando por especie: Gato");
        } else if (query.contains("ave")) {
            currentEspecieFilter = "Ave";
            android.util.Log.d("SearchFragment", "🔍 Filtrando por especie: Ave");
        } else if (query.contains("conejo")) {
            currentEspecieFilter = "Conejo";
            android.util.Log.d("SearchFragment", "🔍 Filtrando por especie: Conejo");
        }
        // 5. Si no coincide con nada
        else {
            Toast.makeText(getContext(),
                "Búsqueda: '" + query + "'. Intenta: perro, labrador, 2, macho",
                Toast.LENGTH_SHORT).show();
        }

        // Cargar con los filtros detectados
        loadPetsFromApiWithFilters(edadFilter, sexoFilter, razaFilter);
    }

    /**
     * Configurar botones de categorías (Perro, Gato, Ave, Conejo)
     */
    private void setupCategoryButtons(View root) {
        root.findViewById(R.id.btn_categoria_perro).setOnClickListener(v -> {
            currentEspecieFilter = "Perro";
            currentSearchQuery = null;
            searchInput.setText(""); // Limpiar búsqueda
            currentPage = 1;
            loadPetsFromApi();
        });

        root.findViewById(R.id.btn_categoria_gato).setOnClickListener(v -> {
            currentEspecieFilter = "Gato";
            currentSearchQuery = null;
            searchInput.setText(""); // Limpiar búsqueda
            currentPage = 1;
            loadPetsFromApi();
        });

        root.findViewById(R.id.btn_categoria_ave).setOnClickListener(v -> {
            currentEspecieFilter = "Ave";
            currentSearchQuery = null;
            searchInput.setText(""); // Limpiar búsqueda
            currentPage = 1;
            loadPetsFromApi();
        });

        root.findViewById(R.id.btn_categoria_conejo).setOnClickListener(v -> {
            currentEspecieFilter = "Conejo";
            currentSearchQuery = null;
            searchInput.setText(""); // Limpiar búsqueda
            currentPage = 1;
            loadPetsFromApi();
        });
    }

    /**
     * Cargar mascotas desde la API
     */
    private void loadPetsFromApi() {
        loadPetsFromApiWithFilters(null, null, null);
    }

    /**
     * Cargar mascotas desde la API con filtros básicos
     * @param edad Edad exacta (número como String: "2", "5", etc.)
     * @param sexo Sexo ("Macho" o "Hembra")
     * @param raza Raza (búsqueda parcial: "Labrador", "Golden", etc.)
     */
    private void loadPetsFromApiWithFilters(String edad, String sexo, String raza) {
        // Mostrar loading
        progressLoading.setVisibility(View.VISIBLE);
        recyclerPets.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);

        // Construir filtros
        java.util.Map<String, String> filters = new java.util.HashMap<>();

        // Filtro de especie (de los botones de categoría)
        if (currentEspecieFilter != null) {
            filters.put("especie", currentEspecieFilter);
        }

        // Filtro de edad exacta (número)
        if (edad != null) {
            filters.put("edad", edad);
        }

        // Filtro de sexo
        if (sexo != null) {
            filters.put("sexo", sexo);
        }

        // Filtro de raza (búsqueda parcial)
        if (raza != null) {
            filters.put("raza", raza);
        }

        // Paginación
        filters.put("page", String.valueOf(currentPage));
        filters.put("limit", String.valueOf(ITEMS_PER_PAGE));

        android.util.Log.d("SearchFragment", "📡 Llamando API - Filtros: " + filters);

        // Llamar a la API
        com.example.adoptmev5.api.PetsApiService.getPets(filters, new com.example.adoptmev5.api.PetsApiService.PetsCallback() {
            @Override
            public void onSuccess(java.util.List<com.example.adoptmev5.models.Pet> pets, org.json.JSONObject pagination) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressLoading.setVisibility(View.GONE);

                    if (pets == null || pets.isEmpty()) {
                        // Mostrar estado vacío
                        recyclerPets.setVisibility(View.GONE);
                        emptyState.setVisibility(View.VISIBLE);
                        tvResultsCount.setText("0 resultados");
                        android.util.Log.w("SearchFragment", "⚠️ Sin resultados");
                    } else {
                        // Mostrar mascotas
                        recyclerPets.setVisibility(View.VISIBLE);
                        emptyState.setVisibility(View.GONE);
                        petsAdapter.setPets(pets);
                        tvResultsCount.setText(pets.size() + " resultados");

                        android.util.Log.d("SearchFragment", "✅ Cargadas " + pets.size() + " mascotas");
                    }
                });
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null) return;

                getActivity().runOnUiThread(() -> {
                    progressLoading.setVisibility(View.GONE);
                    recyclerPets.setVisibility(View.GONE);
                    emptyState.setVisibility(View.VISIBLE);

                    Toast.makeText(getContext(),
                        "Error: " + error,
                        Toast.LENGTH_LONG).show();

                    android.util.Log.e("SearchFragment", "❌ Error API: " + error);
                });
            }
        });
    }

    /**
     * Manejar click en una mascota
     */
    private void onPetClick(com.example.adoptmev5.models.Pet pet) {
        Intent intent = new Intent(getContext(), PetDetailActivity.class);
        intent.putExtra("pet_id", pet.getId());
        intent.putExtra("pet_name", pet.getName());
        intent.putExtra("pet_description", pet.getDescripcion());
        intent.putExtra("pet_age", pet.getEdad() + " años");
        intent.putExtra("pet_especie", pet.getEspecie());
        intent.putExtra("pet_raza", pet.getRaza());
        intent.putExtra("pet_tamano", pet.getTamano());
        intent.putExtra("pet_sexo", pet.getSexo());
        intent.putExtra("pet_distrito", pet.getDistrito());
        intent.putExtra("pet_foto_url", pet.getFotoUrl());
        intent.putExtra("pet_is_urgent", pet.isUrgent());
        intent.putExtra("pet_weight", "N/A");
        intent.putExtra("pet_height", "N/A");
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargar mascotas al volver (por si cambiaron filtros)
        if (petsAdapter != null) {
            loadPetsFromApi();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
