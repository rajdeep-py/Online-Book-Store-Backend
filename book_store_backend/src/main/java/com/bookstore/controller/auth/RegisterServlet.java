package com.bookstore.controller.auth;

import com.bookstore.service.AuthService;
import com.bookstore.util.JsonUtil;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class RegisterServlet extends HttpServlet {
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
        String fullName = payload.getString("full_name", null);
        String email = payload.getString("email", null);
        String password = payload.getString("password", null);
        String phoneNumber = payload.getString("phone_number", null);
        String address = payload.getString("address", null);
        try {
            int customerId = authService.registerCustomer(fullName, email, password, phoneNumber, address);
            if (customerId <= 0) {
                JsonUtil.writeJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Json.createObjectBuilder().add("error", "Invalid data or email exists").build());
                return;
            }
            JsonUtil.writeJson(response, HttpServletResponse.SC_CREATED,
                Json.createObjectBuilder().add("customer_id", customerId).build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Registration failed").build());
        }
    }
}
