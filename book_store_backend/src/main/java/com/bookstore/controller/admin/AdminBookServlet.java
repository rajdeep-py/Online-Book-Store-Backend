package com.bookstore.controller.admin;

import com.bookstore.util.JsonUtil;
import jakarta.json.Json;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminBookServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonUtil.writeJson(response, HttpServletResponse.SC_GONE,
            Json.createObjectBuilder().add("error", "Use /api/books" ).build());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonUtil.writeJson(response, HttpServletResponse.SC_GONE,
            Json.createObjectBuilder().add("error", "Use /api/books" ).build());
    }
}
