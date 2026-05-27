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
        String fullName = payload.containsKey("full_name") && !payload.isNull("full_name") ? payload.getString("full_name") : "";
        String email = payload.containsKey("email") && !payload.isNull("email") ? payload.getString("email") : "";
        String password = payload.containsKey("password") && !payload.isNull("password") ? payload.getString("password") : "";
        String phoneNumber = payload.containsKey("phone_number") && !payload.isNull("phone_number") ? payload.getString("phone_number") : "";
        String address = payload.containsKey("address") && !payload.isNull("address") ? payload.getString("address") : "";
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
