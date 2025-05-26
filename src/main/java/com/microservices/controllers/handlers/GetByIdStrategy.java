package com.microservices.controllers.handlers;

import com.microservices.daos.IUserDAO;
import com.microservices.daos.UserDAO;
import com.microservices.helpers.JsonResponseHelper;
import com.microservices.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class GetByIdStrategy implements IRequestHandlerStrategy {
    private final IUserDAO userDAO;

    public GetByIdStrategy() {
        this.userDAO = new UserDAO();
    }

    @Override
    public boolean canHandle(String pathInfo) {
        return pathInfo != null && pathInfo.matches("^/\\d+$");
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String pathInfo = req.getPathInfo();
        int id = Integer.parseInt(pathInfo.substring(1));
        User user = userDAO.getUserById(id);

        if (user != null) {
            JsonResponseHelper.writeJson(resp, HttpServletResponse.SC_OK, user);
        } else {
            JsonResponseHelper.writeError(resp, HttpServletResponse.SC_NOT_FOUND, "Usuario no encontrado");
        }
    }
}
