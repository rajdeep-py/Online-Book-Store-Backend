package com.bookstore.controller.admin;

import com.bookstore.dao.AboutUsDAO;
import com.bookstore.model.AboutUs;
import com.bookstore.util.JsonUtil;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class AboutUsServlet extends HttpServlet {
    private final AboutUsDAO aboutUsDAO = new AboutUsDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = getIdParam(request);
        try {
            if (idParam != null) {
                AboutUs about = aboutUsDAO.getById(Integer.parseInt(idParam));
                if (about == null) {
                    JsonUtil.writeJson(response, HttpServletResponse.SC_NOT_FOUND,
                        Json.createObjectBuilder().add("error", "Not found").build());
                    return;
                }
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK, toJson(about));
                return;
            }
            List<AboutUs> items = aboutUsDAO.getAll();
            JsonArrayBuilder array = Json.createArrayBuilder();
            for (AboutUs about : items) {
                array.add(toJson(about));
            }
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                Json.createObjectBuilder().add("items", array).build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Failed to load about us").build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject payload = JsonUtil.readJsonObject(request);
        AboutUs about = mapFromPayload(payload);
        try {
            int id = aboutUsDAO.create(about);
            JsonUtil.writeJson(response, HttpServletResponse.SC_CREATED,
                Json.createObjectBuilder().add("about_id", id).build());
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
                Json.createObjectBuilder().add("error", "Missing about id").build());
            return;
        }
        JsonObject payload = JsonUtil.readJsonObject(request);
        AboutUs about = mapFromPayload(payload);
        about.setAboutId(Integer.parseInt(idParam));
        try {
            aboutUsDAO.update(about);
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
                Json.createObjectBuilder().add("error", "Missing about id").build());
            return;
        }
        try {
            aboutUsDAO.delete(Integer.parseInt(idParam));
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

    private AboutUs mapFromPayload(JsonObject payload) {
        AboutUs about = new AboutUs();
        about.setCompanyName(payload.getString("company_name", null));
        about.setCompanyTagline(payload.getString("company_tagline", null));
        about.setCompanyDescription(payload.getString("company_description", null));
        about.setDirectorMessage(payload.getString("director_message", null));
        about.setDirectorName(payload.getString("director_name", null));
        about.setMission(payload.getString("mission", null));
        about.setVision(payload.getString("vision", null));
        about.setPartnersJson(payload.containsKey("partners") ? payload.getJsonArray("partners").toString() : null);
        about.setPhoneNo(payload.getString("phone_no", null));
        about.setEmailId(payload.getString("email_id", null));
        about.setAddress(payload.getString("address", null));
        return about;
    }

    private JsonObject toJson(AboutUs about) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("about_id", about.getAboutId());
        builder.add("company_name", about.getCompanyName());
        builder.add("company_tagline", about.getCompanyTagline() == null ? "" : about.getCompanyTagline());
        builder.add("company_description", about.getCompanyDescription() == null ? "" : about.getCompanyDescription());
        builder.add("director_message", about.getDirectorMessage() == null ? "" : about.getDirectorMessage());
        builder.add("director_name", about.getDirectorName() == null ? "" : about.getDirectorName());
        builder.add("mission", about.getMission() == null ? "" : about.getMission());
        builder.add("vision", about.getVision() == null ? "" : about.getVision());
        builder.add("partners", parsePartners(about.getPartnersJson()));
        builder.add("phone_no", about.getPhoneNo() == null ? "" : about.getPhoneNo());
        builder.add("email_id", about.getEmailId() == null ? "" : about.getEmailId());
        builder.add("address", about.getAddress() == null ? "" : about.getAddress());
        return builder.build();
    }

    private JsonArray parsePartners(String partnersJson) {
        if (partnersJson == null || partnersJson.isBlank()) {
            return Json.createArrayBuilder().build();
        }
        try (JsonReader reader = Json.createReader(new java.io.StringReader(partnersJson))) {
            return reader.readArray();
        }
    }
}
