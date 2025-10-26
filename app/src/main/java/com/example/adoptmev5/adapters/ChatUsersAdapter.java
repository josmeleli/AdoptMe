package com.example.adoptmev5.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptmev5.R;
import com.example.adoptmev5.models.ChatUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatUsersAdapter extends RecyclerView.Adapter<ChatUsersAdapter.UserViewHolder> {

    private List<ChatUser> users;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(ChatUser user);
    }

    public ChatUsersAdapter(List<ChatUser> users, OnUserClickListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        ChatUser user = users.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateUsers(List<ChatUser> newUsers) {
        this.users = newUsers;
        notifyDataSetChanged();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView userName;
        TextView lastMessage;
        TextView messageTime;
        TextView unreadBadge;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userAvatar = itemView.findViewById(R.id.user_avatar);
            userName = itemView.findViewById(R.id.user_name);
            lastMessage = itemView.findViewById(R.id.last_message);
            messageTime = itemView.findViewById(R.id.message_time);
            unreadBadge = itemView.findViewById(R.id.unread_badge);
        }

        void bind(ChatUser user, OnUserClickListener listener) {
            userName.setText(user.getName());
            lastMessage.setText(user.getLastMessage());
            messageTime.setText(formatTime(user.getLastMessageTime()));

            // Mostrar badge de no leídos
            if (user.getUnreadCount() > 0) {
                unreadBadge.setVisibility(View.VISIBLE);
                unreadBadge.setText(String.valueOf(user.getUnreadCount()));
            } else {
                unreadBadge.setVisibility(View.GONE);
            }

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(user);
                }
            });
        }

        private String formatTime(String dateTimeString) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                Date date = inputFormat.parse(dateTimeString);

                // Verificar si es hoy
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String today = dateFormat.format(new Date());
                String messageDate = dateFormat.format(date);

                if (today.equals(messageDate)) {
                    return outputFormat.format(date);
                } else {
                    SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
                    return dayFormat.format(date);
                }
            } catch (Exception e) {
                return dateTimeString;
            }
        }
    }
}

