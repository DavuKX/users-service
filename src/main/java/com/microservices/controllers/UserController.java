package com.microservices.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.daos.UserDAO;
import com.microservices.dtos.CreateUserDTO;
import com.microservices.dtos.LoginUserDTO;
import com.microservices.dtos.UserDTO;
import com.microservices.models.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/auth/*")
public class UserController extends HttpServlet {
    private ObjectMapper mapper;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        mapper = new ObjectMapper();
        userDAO = new UserDAO();
    }

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);

        String path = req.getPathInfo();

        if (path == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\": \"Ruta no encontrada\"}");
            return;
        }

        switch (path) {
            case "/register":
                handleRegister(req, resp);
                break;
            case "/login":
                handleLogin(req, resp);
                break;
            default:
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Ruta no encontrada\"}");
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setContentType("application/json");

        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/auth
                resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                resp.getWriter().write("{\"error\": \"Para obtener un usuario, especifique el ID\"}");
            } else {
                // GET /api/auth/{id}
                int id = Integer.parseInt(pathInfo.substring(1));
                User user = userDAO.getUserById(id);

                if (user != null) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    mapper.writeValue(resp.getWriter(), user);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\": \"Usuario no encontrado\"}");
                }
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"ID de usuario inválido. Debe ser un número\"}");
        } catch (SQLException | ClassNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setCorsHeaders(resp);
        resp.setContentType("application/json");

        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"Se requiere el ID del usuario\"}");
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));

            User updatedData = mapper.readValue(req.getReader(), User.class);

            if (updatedData.getId() != id) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\": \"El ID del usuario no coincide\"}");
                return;
            }

            User existingUser = userDAO.getUserById(id);

            if (existingUser == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Usuario no encontrado\"}");
                return;
            }

            User updatedUser = userDAO.updateUser(updatedData);

            resp.setStatus(HttpServletResponse.SC_OK);
            mapper.writeValue(resp.getWriter(), updatedUser);

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"ID de usuario inválido\"}");
        } catch (SQLException | ClassNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CreateUserDTO userDTO = mapper.readValue(req.getInputStream(), CreateUserDTO.class);

        try {
            User user = userDAO.save(userDTO);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), user);
        } catch (SQLException | ClassNotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginUserDTO loginRequest = mapper.readValue(req.getInputStream(), LoginUserDTO.class);

        try {
            UserDTO user = userDAO.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

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
