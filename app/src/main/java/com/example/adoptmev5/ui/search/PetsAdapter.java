package com.example.adoptmev5.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.adoptmev5.R;
import com.example.adoptmev5.models.Pet;

import java.util.ArrayList;
import java.util.List;

public class PetsAdapter extends RecyclerView.Adapter<PetsAdapter.PetViewHolder> {

    private List<Pet> pets = new ArrayList<>();
    private OnPetClickListener listener;

    public interface OnPetClickListener {
        void onPetClick(Pet pet);
    }

    public PetsAdapter(OnPetClickListener listener) {
        this.listener = listener;
    }

    public void setPets(List<Pet> pets) {
        this.pets = pets != null ? pets : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pet_card, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = pets.get(position);
        holder.bind(pet, listener);
    }

    @Override
    public int getItemCount() {
        return pets.size();
    }

    static class PetViewHolder extends RecyclerView.ViewHolder {
        private ImageView petImage;
        private TextView petName;
        private TextView petInfo;
        private TextView petAge;
        private TextView petDistrito;
        private Button btnAdopt;
        private View urgentBadge;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            petImage = itemView.findViewById(R.id.pet_image);
            petName = itemView.findViewById(R.id.pet_name);
            petInfo = itemView.findViewById(R.id.pet_info);
            petAge = itemView.findViewById(R.id.pet_age);
            petDistrito = itemView.findViewById(R.id.pet_distrito);
            btnAdopt = itemView.findViewById(R.id.btn_adopt);
            urgentBadge = itemView.findViewById(R.id.urgent_badge);
        }

        public void bind(Pet pet, OnPetClickListener listener) {
            petName.setText(pet.getName());
            petInfo.setText(pet.getEspecie() + " • " + pet.getRaza());
            petAge.setText(pet.getEdad() + " años");
            petDistrito.setText(pet.getDistrito());

            // Mostrar badge urgente
            urgentBadge.setVisibility(pet.isUrgent() ? View.VISIBLE : View.GONE);

            // Cargar imagen desde URL usando Glide
            String fotoUrl = pet.getFotoUrl();
            if (fotoUrl != null && !fotoUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(fotoUrl)
                        .placeholder(getPlaceholderByEspecie(pet.getEspecie()))
                        .error(getPlaceholderByEspecie(pet.getEspecie()))
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(petImage);
            } else {
                // Usar placeholder si no hay URL
                petImage.setImageResource(getPlaceholderByEspecie(pet.getEspecie()));
            }

            // Click listeners
            btnAdopt.setOnClickListener(v -> {
                if (listener != null) listener.onPetClick(pet);
            });

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onPetClick(pet);
            });
        }

        /**
         * Obtener placeholder según la especie
         */
        private int getPlaceholderByEspecie(String especie) {
            if (especie == null) return R.drawable.search1;
            
            if (especie.equalsIgnoreCase("Perro")) {
                return R.drawable.search1;
            } else if (especie.equalsIgnoreCase("Gato")) {
                return R.drawable.search2;
            } else if (especie.equalsIgnoreCase("Ave")) {
                return R.drawable.search5;
            } else if (especie.equalsIgnoreCase("Conejo")) {
                return R.drawable.search3;
            } else {
                return R.drawable.search1;
            }
        }
    }
}

