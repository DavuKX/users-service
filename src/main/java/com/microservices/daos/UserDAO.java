package com.microservices.daos;

import com.microservices.config.DatabaseConnection;
import com.microservices.dtos.CreateUserDTO;
import com.microservices.dtos.UserDTO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {
    public void save(CreateUserDTO user) throws SQLException, ClassNotFoundException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO usuario (nombre, apellido, correo, contrasena, rol) VALUES (?, ?, ?, ?, ?)"
             )) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getRol());

            stmt.executeUpdate();
        }
    }

    public UserDTO authenticate(String userEmail, String userPassword) throws SQLException, ClassNotFoundException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT id, nombre, apellido, correo, rol FROM usuario WHERE correo = ? AND contrasena = ?"
             )) {
            stmt.setString(1, userEmail);
            stmt.setString(2, userPassword);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("nombre");
                String lastName = rs.getString("apellido");
                String email = rs.getString("userEmail");
                String rol = rs.getString("rol");
                return new UserDTO(id, name, lastName, email, userPassword, rol);
            } else {
                return null;
            }
        }
    }
}
