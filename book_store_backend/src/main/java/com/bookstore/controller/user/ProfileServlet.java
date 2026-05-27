package com.bookstore.controller.user;

import com.bookstore.service.AuthService;
import com.bookstore.model.User;
import com.bookstore.util.JsonUtil;
import com.bookstore.util.SessionUtil;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;

public class ProfileServlet extends HttpServlet {
    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer customerId = session != null ? SessionUtil.getCustomerId(session) : null;
        if (customerId == null) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                Json.createObjectBuilder().add("error", "Customer login required").build());
            return;
        }
        try {
            User user = authService.getCustomerProfile(customerId);
            if (user == null) {
                JsonUtil.writeJson(response, HttpServletResponse.SC_NOT_FOUND,
                    Json.createObjectBuilder().add("error", "User not found").build());
                return;
            }
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK, toJson(user));
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Failed to load profile").build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doPut(request, response);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer customerId = session != null ? SessionUtil.getCustomerId(session) : null;
        if (customerId == null) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                Json.createObjectBuilder().add("error", "Customer login required").build());
            return;
        }
        JsonObject payload = JsonUtil.readJsonObject(request);
        String fullName = payload.containsKey("full_name") && !payload.isNull("full_name") ? payload.getString("full_name") : null;
        String email = payload.containsKey("email") && !payload.isNull("email") ? payload.getString("email") : null;
        String password = payload.containsKey("password") && !payload.isNull("password") ? payload.getString("password") : null;
        String phoneNumber = payload.containsKey("phone_number") && !payload.isNull("phone_number") ? payload.getString("phone_number") : null;
        String address = payload.containsKey("address") && !payload.isNull("address") ? payload.getString("address") : null;
        String profilePhotoRaw = payload.containsKey("profile_photo") && !payload.isNull("profile_photo") ? payload.getString("profile_photo") : null;
        
        String profilePhotoPath = null;
        if (profilePhotoRaw != null && profilePhotoRaw.startsWith("data:image/")) {
            try {
                String[] parts = profilePhotoRaw.split(",");
                if (parts.length == 2) {
                    byte[] imageBytes = java.util.Base64.getDecoder().decode(parts[1]);
                    
                    // Create directory if not exists
                    String uploadDir = System.getProperty("user.home") + java.io.File.separator + "bookstore_uploads" + java.io.File.separator + "profile";
                    java.io.File dir = new java.io.File(uploadDir);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    
                    String fileName = "customer_" + customerId + ".png";
                    java.io.File file = new java.io.File(dir, fileName);
                    
                    try (java.io.FileOutputStream fos = new java.io.FileOutputStream(file)) {
                        fos.write(imageBytes);
                    }
                    
                    // Set path to be saved in DB
                    profilePhotoPath = "/uploads/profile/" + fileName;
                }
            } catch (Exception e) {
                // Ignore base64 parse errors, let photo remain null or throw internal error
                e.printStackTrace();
            }
        }

        try {
            boolean success = authService.updateCustomerProfile(customerId, fullName, email, password, phoneNumber, address, profilePhotoPath);
            if (!success) {
                JsonUtil.writeJson(response, HttpServletResponse.SC_BAD_REQUEST,
                    Json.createObjectBuilder().add("error", "Update failed. Email may already exist or password is weak.").build());
                return;
            }
            // Refresh session info if name or email changed
            User updatedUser = authService.getCustomerProfile(customerId);
            SessionUtil.setCustomer(session, updatedUser);

            JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                Json.createObjectBuilder().add("message", "Profile updated successfully").build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Profile update failed").build());
        }
    }

    private JsonObject toJson(User user) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("customer_id", user.getCustomerId());
        builder.add("full_name", user.getFullName());
        builder.add("email", user.getEmail());
        builder.add("phone_number", user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        builder.add("profile_photo", user.getProfilePhoto() != null ? user.getProfilePhoto() : "");
        builder.add("address", user.getAddress() != null ? user.getAddress() : "");
        return builder.build();
    }
}
