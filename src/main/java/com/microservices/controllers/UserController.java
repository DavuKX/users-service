package com.microservices.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.daos.UserDAO;
import com.microservices.dtos.CreateUserDTO;
import com.microservices.dtos.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/users")
public class UserController extends HttpServlet {
    private ObjectMapper mapper;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        mapper = new ObjectMapper();
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        CreateUserDTO user = mapper.readValue(req.getInputStream(), CreateUserDTO.class);

        try {
            userDAO.save(user);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write("{\"message\": \"Usuario registrado\"}");
        } catch (SQLException | ClassNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userEmail = req.getParameter("email");
        String userPassword = req.getParameter("password");

        try {
            UserDTO user = userDAO.authenticate(userEmail, userPassword);

            if (user != null) {
                resp.setContentType("application/json");
                mapper.writeValue(resp.getWriter(), user);
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\": \"Correo o contraseña incorrecta\"}");
            }
        } catch (SQLException | ClassNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
