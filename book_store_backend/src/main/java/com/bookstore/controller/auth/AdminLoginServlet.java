package com.bookstore.controller.auth;

import com.bookstore.service.AuthService;
import com.bookstore.util.JsonUtil;
import com.bookstore.util.SessionUtil;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class AdminLoginServlet extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonUtil.writeJson(response, HttpServletResponse.SC_METHOD_NOT_ALLOWED,
            Json.createObjectBuilder().add("error", "POST only").build());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject payload = JsonUtil.readJsonObject(request);
        String email = payload.getString("email", null);
        String password = payload.getString("password", null);
        try {
            var admin = authService.loginAdmin(email, password);
            if (admin == null) {
                JsonUtil.writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                    Json.createObjectBuilder().add("error", "Invalid email or password").build());
                return;
            }
            HttpSession session = request.getSession(true);
            SessionUtil.setAdmin(session, admin);
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                Json.createObjectBuilder()
                    .add("admin_id", admin.getAdminId())
                    .add("admin_name", admin.getAdminName())
                    .add("admin_email", admin.getAdminEmail())
                    .build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Login failed").build());
        }
    }
}
