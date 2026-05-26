package com.bookstore.controller.user;

import com.bookstore.dao.ContactMessageDAO;
import com.bookstore.model.ContactMessage;
import com.bookstore.util.JsonUtil;
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

public class ContactMessageServlet extends HttpServlet {
    private final ContactMessageDAO messageDAO = new ContactMessageDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = getIdParam(request);
        try {
            if (idParam != null) {
                ContactMessage message = messageDAO.getById(Integer.parseInt(idParam));
                if (message == null) {
                    JsonUtil.writeJson(response, HttpServletResponse.SC_NOT_FOUND,
                        Json.createObjectBuilder().add("error", "Not found").build());
                    return;
                }
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK, toJson(message));
                return;
            }
            List<ContactMessage> messages = messageDAO.getAll();
            JsonArrayBuilder array = Json.createArrayBuilder();
            for (ContactMessage message : messages) {
                array.add(toJson(message));
            }
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                Json.createObjectBuilder().add("items", array).build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Failed to load messages").build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject payload = JsonUtil.readJsonObject(request);
        ContactMessage message = new ContactMessage();
        message.setName(payload.getString("name", null));
        message.setEmail(payload.getString("email", null));
        message.setPhoneNo(payload.getString("phone_no", null));
        message.setSubject(payload.getString("subject", null));
        message.setMessage(payload.getString("message", null));
        message.setStatus(payload.getString("status", "NEW"));
        try {
            int id = messageDAO.create(message);
            JsonUtil.writeJson(response, HttpServletResponse.SC_CREATED,
                Json.createObjectBuilder().add("message_id", id).build());
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
                Json.createObjectBuilder().add("error", "Missing message id").build());
            return;
        }
        JsonObject payload = JsonUtil.readJsonObject(request);
        ContactMessage message = new ContactMessage();
        message.setMessageId(Integer.parseInt(idParam));
        message.setName(payload.getString("name", null));
        message.setEmail(payload.getString("email", null));
        message.setPhoneNo(payload.getString("phone_no", null));
        message.setSubject(payload.getString("subject", null));
        message.setMessage(payload.getString("message", null));
        message.setStatus(payload.getString("status", null));
        try {
            messageDAO.update(message);
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
                Json.createObjectBuilder().add("error", "Missing message id").build());
            return;
        }
        try {
            messageDAO.delete(Integer.parseInt(idParam));
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

    private JsonObject toJson(ContactMessage message) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("message_id", message.getMessageId());
        builder.add("name", message.getName());
        builder.add("email", message.getEmail());
        builder.add("phone_no", message.getPhoneNo() == null ? "" : message.getPhoneNo());
        builder.add("subject", message.getSubject() == null ? "" : message.getSubject());
        builder.add("message", message.getMessage());
        builder.add("status", message.getStatus());
        return builder.build();
    }
}
