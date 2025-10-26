package com.example.adoptmev5.ui.notifications;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptmev5.R;
import com.example.adoptmev5.models.Notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    private List<Notification> notifications = new ArrayList<>();
    private OnNotificationClickListener listener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public NotificationsAdapter(OnNotificationClickListener listener) {
        this.listener = listener;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification, listener);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView title, message, date;
        View unreadIndicator;
        ImageView icon;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notification_title);
            message = itemView.findViewById(R.id.notification_message);
            date = itemView.findViewById(R.id.notification_date);
            unreadIndicator = itemView.findViewById(R.id.unread_indicator);
            icon = itemView.findViewById(R.id.notification_icon);
        }

        public void bind(Notification notification, OnNotificationClickListener listener) {
            title.setText(notification.getTitle());
            message.setText(notification.getMessage());
            date.setText(getTimeAgo(notification.getCreatedAt()));

            // Mostrar indicador de no leído
            if (notification.isRead()) {
                unreadIndicator.setVisibility(View.GONE);
                // Hacer el texto menos prominente
                title.setAlpha(0.7f);
                message.setAlpha(0.7f);
            } else {
                unreadIndicator.setVisibility(View.VISIBLE);
                title.setAlpha(1.0f);
                message.setAlpha(1.0f);
            }

            // Configurar icono según tipo
            if ("nueva_solicitud".equals(notification.getType())) {
                icon.setImageResource(android.R.drawable.ic_dialog_email);
            } else {
                icon.setImageResource(android.R.drawable.ic_dialog_info);
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onNotificationClick(notification);
                }
            });
        }

        private String getTimeAgo(String createdAt) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = sdf.parse(createdAt);
                if (date == null) return createdAt;

                long diff = System.currentTimeMillis() - date.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                if (seconds < 60) {
                    return "Ahora";
                } else if (minutes < 60) {
                    return "Hace " + minutes + (minutes == 1 ? " minuto" : " minutos");
                } else if (hours < 24) {
                    return "Hace " + hours + (hours == 1 ? " hora" : " horas");
                } else if (days < 7) {
                    return "Hace " + days + (days == 1 ? " día" : " días");
                } else {
                    SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                    return displayFormat.format(date);
                }
            } catch (ParseException e) {
                return createdAt;
            }
        }
    }
}

