package com.bookstore.controller.auth;

import com.bookstore.util.JsonUtil;
import jakarta.json.Json;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
            Json.createObjectBuilder().add("message", "Logged out").build());
    }
}
