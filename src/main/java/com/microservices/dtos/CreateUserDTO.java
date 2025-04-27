package com.microservices.dtos;

public class CreateUserDTO {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String rol;

    public CreateUserDTO() {}

    public CreateUserDTO(String name, String lastName, String email, String password, String rol) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRol() {
        return rol;
    }
}
