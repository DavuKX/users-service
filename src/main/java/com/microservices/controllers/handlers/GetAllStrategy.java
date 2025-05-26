package com.microservices.controllers.handlers;

import com.microservices.daos.IUserDAO;
import com.microservices.daos.UserDAO;
import com.microservices.helpers.JsonResponseHelper;
import com.microservices.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public class GetAllStrategy implements IRequestHandlerStrategy {
    private final IUserDAO userDAO;

    public GetAllStrategy() {
        this.userDAO = new UserDAO();
    }

    @Override
    public boolean canHandle(String pathInfo) {
        return pathInfo == null || pathInfo.equals("/") || pathInfo.equals("/all");
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        List<User> users = userDAO.getAllUsers();
        JsonResponseHelper.writeJson(resp, HttpServletResponse.SC_OK, users);
    }
}
