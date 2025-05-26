package com.microservices.daos;

import com.microservices.config.DatabaseConnection;
import com.microservices.dtos.CreateUserDTO;
import com.microservices.dtos.UserDTO;
import com.microservices.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {
    public User save(CreateUserDTO userDTO) throws SQLException, ClassNotFoundException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO usuario (nombre, apellido, correo, contrasena, rol, estado) VALUES (?, ?, ?, ?, ?, ?)",
                     PreparedStatement.RETURN_GENERATED_KEYS // Esto es importante para obtener el ID generado
             )) {

            stmt.setString(1, userDTO.getNombre());
            stmt.setString(2, userDTO.getApellido());
            stmt.setString(3, userDTO.getCorreo());
            stmt.setString(4, userDTO.getContrasena());
            stmt.setString(5, userDTO.getRol());
            stmt.setString(6, "activo");

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("No se pudo guardar el usuario.");
            }

            // Obtener el ID generado automáticamente
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);

                    // Ahora, hacer una consulta para obtener el usuario completo desde la base de datos
                    return getUserById(generatedId);
                } else {
                    throw new SQLException("No se pudo obtener el ID generado.");
                }
            }
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
                String email = rs.getString("correo");
                String rol = rs.getString("rol");
                return new UserDTO(id, name, lastName, email, rol);
            } else {
                return null;
            }
        }
    }

    public User getUserById(int userId) throws SQLException, ClassNotFoundException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT id, nombre, apellido, correo, contrasena, rol, estado FROM usuario WHERE id = ?"
             )) {
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNombre(rs.getString("nombre"));
                user.setApellido(rs.getString("apellido"));
                user.setCorreo(rs.getString("correo"));
                user.setContrasena(rs.getString("contrasena"));
                user.setRol(rs.getString("rol"));
                user.setEstado(rs.getString("estado"));
                return user;
            } else {
                return null;
            }
        }
    }

    public List<User> getAllUsers() throws SQLException, ClassNotFoundException {
        List<User> users = new ArrayList<>();

        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT id, nombre, apellido, correo, contrasena, rol, estado FROM usuario"
             )) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setNombre(rs.getString("nombre"));
                user.setApellido(rs.getString("apellido"));
                user.setCorreo(rs.getString("correo"));
                user.setContrasena(rs.getString("contrasena"));
                user.setRol(rs.getString("rol"));
                user.setEstado(rs.getString("estado"));
                users.add(user);
            }
        }

        return users;
    }

    public User updateUser(User user) throws SQLException, ClassNotFoundException {
        try (Connection connection = DatabaseConnection.getInstance().getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE usuario SET nombre = ?, apellido = ?, correo = ?, contrasena = ?, rol = ?, estado = ? WHERE id = ?"
             )) {

            stmt.setString(1, user.getNombre());
            stmt.setString(2, user.getApellido());
            stmt.setString(3, user.getCorreo());
            stmt.setString(4, user.getContrasena());
            stmt.setString(5, user.getRol());
            stmt.setString(6, user.getEstado());
            stmt.setInt(7, user.getId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                return getUserById(user.getId());
            } else {
                return null;
            }
        }
    }
}
