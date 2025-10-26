package com.example.adoptmev5.models;

public class UserChat {
    private int user_id;
    private String email;
    private String name;
    private String role;
    private String last_message;
    private String last_message_time;
    private boolean is_last_message_from_user;
    private int unread_count;

    // Constructor vacío
    public UserChat() {
    }

    // Getters y Setters
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLast_message() {
        return last_message;
    }

    public void setLast_message(String last_message) {
        this.last_message = last_message;
    }

    public String getLast_message_time() {
        return last_message_time;
    }

    public void setLast_message_time(String last_message_time) {
        this.last_message_time = last_message_time;
    }

    public boolean is_last_message_from_user() {
        return is_last_message_from_user;
    }

    public void setIs_last_message_from_user(boolean is_last_message_from_user) {
        this.is_last_message_from_user = is_last_message_from_user;
    }

    public int getUnread_count() {
        return unread_count;
    }

    public void setUnread_count(int unread_count) {
        this.unread_count = unread_count;
    }
}

