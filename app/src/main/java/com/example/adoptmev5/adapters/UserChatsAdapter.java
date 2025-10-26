package com.example.adoptmev5.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adoptmev5.R;
import com.example.adoptmev5.models.UserChat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class UserChatsAdapter extends RecyclerView.Adapter<UserChatsAdapter.UserChatViewHolder> {

    private Context context;
    private List<UserChat> userChats;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(UserChat userChat);
    }

    public UserChatsAdapter(Context context, List<UserChat> userChats, OnUserClickListener listener) {
        this.context = context;
        this.userChats = userChats;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_chat, parent, false);
        return new UserChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserChatViewHolder holder, int position) {
        UserChat userChat = userChats.get(position);

        holder.userName.setText(userChat.getName());
        holder.userEmail.setText(userChat.getEmail());
        holder.lastMessage.setText(userChat.getLast_message());
        holder.messageTime.setText(formatTime(userChat.getLast_message_time()));

        // Mostrar badge de no leídos
        if (userChat.getUnread_count() > 0) {
            holder.unreadBadge.setVisibility(View.VISIBLE);
            holder.unreadBadge.setText(String.valueOf(userChat.getUnread_count()));
        } else {
            holder.unreadBadge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onUserClick(userChat);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userChats.size();
    }

    private String formatTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return "";
        }

        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = inputFormat.parse(dateTime);

            // Si es hoy, mostrar solo hora
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            // Comprobación simple: si la fecha es reciente, mostrar hora
            long diffInMillis = System.currentTimeMillis() - date.getTime();
            long hours = diffInMillis / (1000 * 60 * 60);

            if (hours < 24) {
                return timeFormat.format(date);
            } else {
                return dateFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }

    public void updateUserChats(List<UserChat> newUserChats) {
        this.userChats = newUserChats;
        notifyDataSetChanged();
    }

    static class UserChatViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        TextView userEmail;
        TextView lastMessage;
        TextView messageTime;
        TextView unreadBadge;

        public UserChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name);
            userEmail = itemView.findViewById(R.id.user_email);
            lastMessage = itemView.findViewById(R.id.last_message);
            messageTime = itemView.findViewById(R.id.message_time);
            unreadBadge = itemView.findViewById(R.id.unread_badge);
        }
    }
}

