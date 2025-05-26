package com.microservices.factories;

import com.microservices.models.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserFactory implements IUserFactory {

    @Override
    public User fromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setNombre(rs.getString("nombre"));
        user.setApellido(rs.getString("apellido"));
        user.setCorreo(rs.getString("correo"));
        user.setContrasena(rs.getString("contrasena"));
        user.setRol(rs.getString("rol"));
        user.setEstado(rs.getString("estado"));
        return user;
    }
}
