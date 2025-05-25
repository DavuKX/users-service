package com.microservices.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JsonResponseHelper {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void writeJson(HttpServletResponse resp, int status, Object body) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        mapper.writeValue(resp.getWriter(), body);
    }

    public static void writeError(HttpServletResponse resp, int status, String message) throws IOException {
        writeJson(resp, status, new ErrorResponse(message));
    }

    private static class ErrorResponse {
        public final String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}
