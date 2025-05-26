package com.microservices.controllers.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IRequestHandlerStrategy {
    boolean canHandle(String pathInfo);
    void handle(HttpServletRequest req, HttpServletResponse resp) throws Exception;
}
