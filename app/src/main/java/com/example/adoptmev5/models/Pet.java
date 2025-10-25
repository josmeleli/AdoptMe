package com.example.adoptmev5.models;

public class Pet {
    private int id;
    private String name;
    private String especie;
    private String raza;
    private int edad;
    private String tamano;
    private String sexo;
    private String descripcion;
    private String foto_url;
    private String distrito;
    private boolean is_urgent;
    private int priority;
    private String estado;
    private String created_at;
    private String etiqueta_urgencia;
    private String categoria_edad;

    // Constructor vacío
    public Pet() {
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEspecie() { return especie; }
    public String getRaza() { return raza; }
    public int getEdad() { return edad; }
    public String getTamano() { return tamano; }
    public String getSexo() { return sexo; }
    public String getDescripcion() { return descripcion; }
    public String getFotoUrl() { return foto_url; }
    public String getDistrito() { return distrito; }
    public boolean isUrgent() { return is_urgent; }
    public int getPriority() { return priority; }
    public String getEstado() { return estado; }
    public String getCreatedAt() { return created_at; }
    public String getEtiquetaUrgencia() { return etiqueta_urgencia; }
    public String getCategoriaEdad() { return categoria_edad; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEspecie(String especie) { this.especie = especie; }
    public void setRaza(String raza) { this.raza = raza; }
    public void setEdad(int edad) { this.edad = edad; }
    public void setTamano(String tamano) { this.tamano = tamano; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setFotoUrl(String foto_url) { this.foto_url = foto_url; }
    public void setDistrito(String distrito) { this.distrito = distrito; }
    public void setUrgent(boolean is_urgent) { this.is_urgent = is_urgent; }
    public void setPriority(int priority) { this.priority = priority; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setCreatedAt(String created_at) { this.created_at = created_at; }
    public void setEtiquetaUrgencia(String etiqueta_urgencia) { this.etiqueta_urgencia = etiqueta_urgencia; }
    public void setCategoriaEdad(String categoria_edad) { this.categoria_edad = categoria_edad; }
}

