package com.microservices.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.controllers.handlers.GetAllStrategy;
import com.microservices.controllers.handlers.GetByIdStrategy;
import com.microservices.controllers.handlers.IRequestHandlerStrategy;
import com.microservices.daos.IUserDAO;
import com.microservices.daos.UserDAO;
import com.microservices.dtos.CreateUserDTO;
import com.microservices.dtos.LoginUserDTO;
import com.microservices.dtos.UserDTO;
import com.microservices.factories.JsonResponseFactory;
import com.microservices.models.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/auth/*")
public class UserController extends HttpServlet {
    private ObjectMapper mapper;
    private IUserDAO userDAO;
    private final List<IRequestHandlerStrategy> getHandlers = new ArrayList<>();

    @Override
    public void init() {
        mapper = new ObjectMapper();
        userDAO = new UserDAO();
        getHandlers.add(new GetAllStrategy());
        getHandlers.add(new GetByIdStrategy());
    }

    private void setCorsHeaders(HttpServletResponse resp) {
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(resp);
        String path = req.getPathInfo();

        if (path == null) {
            JsonResponseFactory.writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada");
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
                JsonResponseFactory.writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada");
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(resp);
        resp.setContentType("application/json");

        String pathInfo = req.getPathInfo();

        try {
            for (IRequestHandlerStrategy handler : getHandlers) {
                if (handler.canHandle(pathInfo)) {
                    handler.handle(req, resp);
                    return;
                }
            }

            JsonResponseFactory.writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Ruta no encontrada");
        } catch (SQLException | ClassNotFoundException e) {
            JsonResponseFactory.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            JsonResponseFactory.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Error al procesar la solicitud");
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        setCorsHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setCorsHeaders(resp);
        resp.setContentType("application/json");

        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                JsonResponseFactory.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Se requiere el ID del usuario");
                return;
            }

            int id = Integer.parseInt(pathInfo.substring(1));
            User updatedData = mapper.readValue(req.getReader(), User.class);

            if (updatedData.getId() != id) {
                JsonResponseFactory.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "El ID del usuario no coincide");
                return;
            }

            User existingUser = userDAO.getUserById(id);

            if (existingUser == null) {
                JsonResponseFactory.writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Usuario no encontrado");
                return;
            }

            User updatedUser = userDAO.updateUser(updatedData);
            JsonResponseFactory.writeJson(resp, HttpServletResponse.SC_OK, updatedUser);
        } catch (NumberFormatException e) {
            JsonResponseFactory.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "ID de usuario inválido");
        } catch (SQLException | ClassNotFoundException e) {
            JsonResponseFactory.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            JsonResponseFactory.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Error al procesar la solicitud");
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CreateUserDTO userDTO = mapper.readValue(req.getInputStream(), CreateUserDTO.class);

        try {
            User user = userDAO.save(userDTO);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            mapper.writeValue(resp.getWriter(), user);
        } catch (SQLException | ClassNotFoundException e) {
            JsonResponseFactory.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            JsonResponseFactory.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Ha ocurrido un error, intente más tarde");
        }
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        LoginUserDTO loginRequest = mapper.readValue(req.getInputStream(), LoginUserDTO.class);

        try {
            UserDTO user = userDAO.authenticate(loginRequest.getEmail(), loginRequest.getPassword());

            if (user != null) {
                JsonResponseFactory.writeJson(resp, HttpServletResponse.SC_OK, user);
            } else {
                JsonResponseFactory.writeError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Correo o contraseña incorrecta");
            }
        } catch (SQLException | ClassNotFoundException e) {
            JsonResponseFactory.writeError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (Exception e) {
            JsonResponseFactory.writeError(resp, HttpServletResponse.SC_BAD_REQUEST, "Error al procesar la solicitud");
        }
    }
}
