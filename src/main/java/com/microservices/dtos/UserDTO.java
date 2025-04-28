package com.microservices.dtos;

public class UserDTO {
    private final int id;
    private final String nombre;
    private final String apellido;
    private final String correo;
    private final String rol;

    public UserDTO(int id, String nombre, String apellido, String correo, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.rol = rol;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
    }
}
