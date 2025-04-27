package com.microservices.dtos;

public class CreateUserDTO {
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
    private String rol;

    public CreateUserDTO() {}

    public CreateUserDTO(String nombre, String apellido, String correo, String contrasena, String rol) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
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

    public String getContrasena() {
        return contrasena;
    }

    public String getRol() {
        return rol;
    }
}
