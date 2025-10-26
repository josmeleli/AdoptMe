package com.example.adoptmev5.models;

public class AdoptionRequestResponse {
    private int id;
    private int petId;
    private String status;
    private String createdAt;
    private String petName;
    private String especie;
    private String raza;
    private int edad;
    private String sexo;
    private String imageUrl;
    private String urgencia;
    private String statusText;
    private String statusColor;
    private String createdAtFormatted;
    private String notasAdmin;

    // Constructor vacío
    public AdoptionRequestResponse() {}

    // Getters
    public int getId() { return id; }
    public int getPetId() { return petId; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getPetName() { return petName; }
    public String getEspecie() { return especie; }
    public String getRaza() { return raza; }
    public int getEdad() { return edad; }
    public String getSexo() { return sexo; }
    public String getImageUrl() { return imageUrl; }
    public String getUrgencia() { return urgencia; }
    public String getStatusText() { return statusText; }
    public String getStatusColor() { return statusColor; }
    public String getCreatedAtFormatted() { return createdAtFormatted; }
    public String getNotasAdmin() { return notasAdmin; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setPetId(int petId) { this.petId = petId; }
    public void setStatus(String status) { this.status = status; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public void setPetName(String petName) { this.petName = petName; }
    public void setEspecie(String especie) { this.especie = especie; }
    public void setRaza(String raza) { this.raza = raza; }
    public void setEdad(int edad) { this.edad = edad; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setUrgencia(String urgencia) { this.urgencia = urgencia; }
    public void setStatusText(String statusText) { this.statusText = statusText; }
    public void setStatusColor(String statusColor) { this.statusColor = statusColor; }
    public void setCreatedAtFormatted(String createdAtFormatted) { this.createdAtFormatted = createdAtFormatted; }
    public void setNotasAdmin(String notasAdmin) { this.notasAdmin = notasAdmin; }
}

