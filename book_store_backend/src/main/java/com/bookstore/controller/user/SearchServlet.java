package com.bookstore.controller.user;

import com.bookstore.util.JsonUtil;
import jakarta.json.Json;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SearchServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonUtil.writeJson(response, HttpServletResponse.SC_GONE,
            Json.createObjectBuilder().add("error", "Use /api/books?q=" ).build());
    }
}
