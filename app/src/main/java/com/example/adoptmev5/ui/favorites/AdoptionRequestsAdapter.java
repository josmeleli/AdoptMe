package com.example.adoptmev5.ui.favorites;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.adoptmev5.R;
import com.example.adoptmev5.models.AdoptionRequestResponse;

import java.util.ArrayList;
import java.util.List;

public class AdoptionRequestsAdapter extends RecyclerView.Adapter<AdoptionRequestsAdapter.ViewHolder> {

    private List<AdoptionRequestResponse> requests = new ArrayList<>();
    private OnRequestClickListener listener;

    public interface OnRequestClickListener {
        void onRequestClick(AdoptionRequestResponse request);
    }

    public AdoptionRequestsAdapter(OnRequestClickListener listener) {
        this.listener = listener;
    }

    public void setRequests(List<AdoptionRequestResponse> requests) {
        this.requests = requests != null ? requests : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_adoption_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdoptionRequestResponse request = requests.get(position);
        holder.bind(request, listener);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPetImage;
        private TextView tvPetName;
        private TextView tvPetInfo;
        private TextView tvStatus;
        private TextView tvDate;
        private TextView tvRequestId;
        private View statusIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPetImage = itemView.findViewById(R.id.iv_pet_image);
            tvPetName = itemView.findViewById(R.id.tv_pet_name);
            tvPetInfo = itemView.findViewById(R.id.tv_pet_info);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvRequestId = itemView.findViewById(R.id.tv_request_id);
            statusIndicator = itemView.findViewById(R.id.status_indicator);
        }

        public void bind(AdoptionRequestResponse request, OnRequestClickListener listener) {
            // Nombre de la mascota
            tvPetName.setText(request.getPetName());

            // Info de la mascota
            String info = request.getEspecie() + " • " + request.getRaza() + " • " + request.getEdad() + " años";
            tvPetInfo.setText(info);

            // Estado
            tvStatus.setText(request.getStatusText());
            try {
                int color = Color.parseColor(request.getStatusColor());
                tvStatus.setTextColor(color);
                statusIndicator.setBackgroundColor(color);
            } catch (Exception e) {
                tvStatus.setTextColor(Color.parseColor("#999999"));
                statusIndicator.setBackgroundColor(Color.parseColor("#999999"));
            }

            // Fecha
            tvDate.setText(request.getCreatedAtFormatted());

            // ID de solicitud
            tvRequestId.setText("Solicitud #" + request.getId());

            // Imagen de la mascota
            if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(request.getImageUrl())
                        .placeholder(R.drawable.search1)
                        .error(R.drawable.search1)
                        .centerCrop()
                        .into(ivPetImage);
            } else {
                ivPetImage.setImageResource(R.drawable.search1);
            }

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRequestClick(request);
                }
            });
        }
    }
}

