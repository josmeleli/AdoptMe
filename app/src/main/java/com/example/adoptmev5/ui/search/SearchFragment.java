package com.example.adoptmev5.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.adoptmev5.R;
import com.example.adoptmev5.databinding.FragmentSearchBinding;

public class SearchFragment extends Fragment {

    private FragmentSearchBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Configurar listeners de los botones
        setupListeners();

        return root;
    }

    private void setupListeners() {
        binding.btnAdoptCanela.setOnClickListener(v -> openPetDetail(
                "Canela",
                "Canela es una perrita juguetona de 2 años, muy cariñosa y sociable con niños y otras mascotas.",
                R.drawable.search1,
                "2 años", "12 kg", "45 cm"
        ));

        binding.btnAdoptSebas.setOnClickListener(v -> openPetDetail(
                "Sebas",
                "Sebas es un gato tranquilo y amoroso de 3 años, ideal para departamentos y personas que buscan compañía calmada.",
                R.drawable.search2,
                "3 años", "4.5 kg", "28 cm"
        ));

        binding.btnAdoptCaramelo.setOnClickListener(v -> openPetDetail(
                "Caramelo",
                "Caramelo es un conejo curioso y tierno, perfecto para hogares tranquilos. Tiene 1 año.",
                R.drawable.search3,
                "1 año", "1.2 kg", "20 cm"
        ));

        binding.btnAdoptTigrillo.setOnClickListener(v -> openPetDetail(
                "Tigrillo",
                "Tigrillo es un gato activo y explorador, necesita espacio para jugar y mucho cariño.",
                R.drawable.search4,
                "2 años", "5 kg", "30 cm"
        ));

        binding.btnAdoptPablo.setOnClickListener(v -> openPetDetail(
                "Pablo",
                "Pablo es un perro leal y protector de 4 años, ideal para familias. Le encanta pasear.",
                R.drawable.search5,
                "4 años", "20 kg", "55 cm"
        ));

        binding.btnAdoptMascota.setOnClickListener(v -> openPetDetail(
                "Mascota",
                "Una mascota especial en busca de un hogar lleno de amor. Ven a conocerla y sorpréndete.",
                R.drawable.search6,
                "Edad desconocida", "Peso desconocido", "Altura desconocida"
        ));
    }


    private void openPetDetail(String name, String description, int imageRes,
                               String age, String weight, String height) {
        Intent intent = new Intent(getContext(), com.example.adoptmev5.ui.search.PetDetailActivity.class);
        intent.putExtra("pet_name", name);
        intent.putExtra("pet_description", description);
        intent.putExtra("pet_image", imageRes);

        // Nuevos extras
        intent.putExtra("pet_age", age);
        intent.putExtra("pet_weight", weight);
        intent.putExtra("pet_height", height);

        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
