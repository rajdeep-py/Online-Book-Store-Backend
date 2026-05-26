package com.bookstore.controller.admin;

import com.bookstore.dao.AdminDAO;
import com.bookstore.model.AdminUser;
import com.bookstore.util.JsonUtil;
import com.bookstore.util.PasswordUtil;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AdminUserServlet extends HttpServlet {
    private final AdminDAO adminDAO = new AdminDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = getIdParam(request);
        try {
            if (idParam != null) {
                AdminUser admin = adminDAO.findById(Integer.parseInt(idParam));
                if (admin == null) {
                    JsonUtil.writeJson(response, HttpServletResponse.SC_NOT_FOUND,
                        Json.createObjectBuilder().add("error", "Not found").build());
                    return;
                }
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK, toJson(admin));
                return;
            }
            List<AdminUser> admins = adminDAO.getAll();
            JsonArrayBuilder array = Json.createArrayBuilder();
            for (AdminUser admin : admins) {
                array.add(toJson(admin));
            }
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                Json.createObjectBuilder().add("items", array).build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Failed to load admins").build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject payload = JsonUtil.readJsonObject(request);
        String rawPassword = payload.getString("admin_password", null);
        if (rawPassword == null) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_BAD_REQUEST,
                Json.createObjectBuilder().add("error", "admin_password required").build());
            return;
        }
        AdminUser admin = new AdminUser();
        admin.setAdminName(payload.getString("admin_name", null));
        admin.setAdminEmail(payload.getString("admin_email", null));
        admin.setAdminPassword(PasswordUtil.hashPassword(rawPassword));
        try {
            int adminId = adminDAO.create(admin);
            JsonUtil.writeJson(response, HttpServletResponse.SC_CREATED,
                Json.createObjectBuilder().add("admin_id", adminId).build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Create failed").build());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = getIdParam(request);
        if (idParam == null) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_BAD_REQUEST,
                Json.createObjectBuilder().add("error", "Missing admin id").build());
            return;
        }
        JsonObject payload = JsonUtil.readJsonObject(request);
        String rawPassword = payload.getString("admin_password", null);
        if (rawPassword == null) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_BAD_REQUEST,
                Json.createObjectBuilder().add("error", "admin_password required").build());
            return;
        }
        AdminUser admin = new AdminUser();
        admin.setAdminId(Integer.parseInt(idParam));
        admin.setAdminName(payload.getString("admin_name", null));
        admin.setAdminEmail(payload.getString("admin_email", null));
        admin.setAdminPassword(PasswordUtil.hashPassword(rawPassword));
        try {
            adminDAO.update(admin);
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                Json.createObjectBuilder().add("message", "Updated").build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Update failed").build());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = getIdParam(request);
        if (idParam == null) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_BAD_REQUEST,
                Json.createObjectBuilder().add("error", "Missing admin id").build());
            return;
        }
        try {
            adminDAO.delete(Integer.parseInt(idParam));
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                Json.createObjectBuilder().add("message", "Deleted").build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Delete failed").build());
        }
    }

    private String getIdParam(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path != null && path.length() > 1) {
            return path.substring(1);
        }
        return request.getParameter("id");
    }

    private JsonObject toJson(AdminUser admin) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("admin_id", admin.getAdminId());
        builder.add("admin_name", admin.getAdminName());
        builder.add("admin_email", admin.getAdminEmail());
        return builder.build();
    }
}
