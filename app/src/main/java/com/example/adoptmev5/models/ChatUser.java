package com.example.adoptmev5.models;

public class ChatUser {
    private int userId;
    private String email;
    private String name;
    private String role;
    private String lastMessage;
    private String lastMessageTime;
    private boolean isLastMessageFromUser;
    private int unreadCount;

    public ChatUser() {
    }

    // Getters y Setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public boolean isLastMessageFromUser() {
        return isLastMessageFromUser;
    }

    public void setLastMessageFromUser(boolean lastMessageFromUser) {
        isLastMessageFromUser = lastMessageFromUser;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}

