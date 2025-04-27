package com.microservices.dtos;

public class UserDTO {
    private final int id;
    private final String name;
    private final String lastName;
    private final String email;
    private final String password;
    private final String rol;

    public UserDTO(int id, String name, String lastName, String email, String password, String rol) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    public int getId() {
        return id;
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
