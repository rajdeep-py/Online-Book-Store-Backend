package com.bookstore.controller.admin;

import com.bookstore.dao.UserDAO;
import com.bookstore.model.User;
import com.bookstore.util.FileStorageUtil;
import com.bookstore.util.JsonUtil;
import com.bookstore.util.PasswordUtil;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@MultipartConfig
public class AdminCustomerServlet extends HttpServlet {
    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String idParam = getIdParam(request);
            if (idParam != null) {
                User user = userDAO.findById(Integer.parseInt(idParam));
                if (user == null) {
                    JsonUtil.writeJson(response, HttpServletResponse.SC_NOT_FOUND,
                        Json.createObjectBuilder().add("error", "Not found").build());
                    return;
                }
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK, toJson(user));
                return;
            }
            List<User> users = userDAO.getAllCustomers();
            JsonArrayBuilder array = Json.createArrayBuilder();
            for (User user : users) {
                array.add(toJson(user));
            }
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                Json.createObjectBuilder().add("items", array).build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Failed to load customers").build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if (request.getContentType() != null && request.getContentType().startsWith("multipart/")) {
                User user = parseCustomerFromMultipart(request, false);
                int customerId = userDAO.create(user);
                Part photo = request.getPart("profile_photo");
                if (photo != null && photo.getSize() > 0) {
                    String path = FileStorageUtil.storeCustomerPhoto(request.getServletContext(),
                        customerId, user.getFullName(), photo);
                    user.setCustomerId(customerId);
                    user.setProfilePhoto(path);
                    userDAO.update(user);
                }
                JsonUtil.writeJson(response, HttpServletResponse.SC_CREATED,
                    Json.createObjectBuilder().add("customer_id", customerId).build());
                return;
            }
            User user = parseCustomer(request, false);
            int customerId = userDAO.create(user);
            JsonUtil.writeJson(response, HttpServletResponse.SC_CREATED,
                Json.createObjectBuilder().add("customer_id", customerId).build());
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
                Json.createObjectBuilder().add("error", "Missing customer id").build());
            return;
        }
        try {
            if (request.getContentType() != null && request.getContentType().startsWith("multipart/")) {
                User user = parseCustomerFromMultipart(request, true);
                Part photo = request.getPart("profile_photo");
                if (photo != null && photo.getSize() > 0) {
                    String path = FileStorageUtil.storeCustomerPhoto(request.getServletContext(),
                        user.getCustomerId(), user.getFullName(), photo);
                    user.setProfilePhoto(path);
                }
                userDAO.update(user);
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                    Json.createObjectBuilder().add("message", "Updated").build());
                return;
            }
            User user = parseCustomer(request, true);
            user.setCustomerId(Integer.parseInt(idParam));
            userDAO.update(user);
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
                Json.createObjectBuilder().add("error", "Missing customer id").build());
            return;
        }
        try {
            userDAO.delete(Integer.parseInt(idParam));
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                Json.createObjectBuilder().add("message", "Deleted").build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Delete failed").build());
        }
    }

    private User parseCustomer(HttpServletRequest request, boolean requireId) throws IOException {
        User user = new User();
        JsonObject payload = JsonUtil.readJsonObject(request);
        user.setFullName(payload.getString("full_name", null));
        user.setEmail(payload.getString("email", null));
        String rawPassword = payload.getString("password", null);
        user.setPassword(rawPassword == null ? null : PasswordUtil.hashPassword(rawPassword));
        user.setPhoneNumber(payload.getString("phone_number", null));
        user.setProfilePhoto(payload.getString("profile_photo", null));
        user.setAddress(payload.getString("address", null));
        return user;
    }

    private User parseCustomerFromMultipart(HttpServletRequest request, boolean requireId) {
        User user = new User();
        user.setFullName(request.getParameter("full_name"));
        user.setEmail(request.getParameter("email"));
        String rawPassword = request.getParameter("password");
        user.setPassword(rawPassword == null ? null : PasswordUtil.hashPassword(rawPassword));
        user.setPhoneNumber(request.getParameter("phone_number"));
        user.setAddress(request.getParameter("address"));
        if (requireId) {
            String idParam = getIdParam(request);
            if (idParam != null) {
                user.setCustomerId(Integer.parseInt(idParam));
            }
        }
        return user;
    }

    private String getIdParam(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path != null && path.length() > 1) {
            return path.substring(1);
        }
        return request.getParameter("id");
    }

    private JsonObject toJson(User user) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("customer_id", user.getCustomerId());
        builder.add("full_name", user.getFullName());
        builder.add("email", user.getEmail());
        builder.add("phone_number", user.getPhoneNumber() == null ? "" : user.getPhoneNumber());
        builder.add("profile_photo", user.getProfilePhoto() == null ? "" : user.getProfilePhoto());
        builder.add("address", user.getAddress() == null ? "" : user.getAddress());
        return builder.build();
    }
}
