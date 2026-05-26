package com.bookstore.controller.admin;

import com.bookstore.dao.BusinessChargesDAO;
import com.bookstore.model.BusinessCharges;
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

public class BusinessChargesServlet extends HttpServlet {
    private final BusinessChargesDAO chargesDAO = new BusinessChargesDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = getIdParam(request);
        try {
            if (idParam != null) {
                BusinessCharges charges = chargesDAO.getById(Integer.parseInt(idParam));
                if (charges == null) {
                    JsonUtil.writeJson(response, HttpServletResponse.SC_NOT_FOUND,
                        Json.createObjectBuilder().add("error", "Not found").build());
                    return;
                }
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK, toJson(charges));
                return;
            }
            List<BusinessCharges> items = chargesDAO.getAll();
            JsonArrayBuilder array = Json.createArrayBuilder();
            for (BusinessCharges charges : items) {
                array.add(toJson(charges));
            }
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                Json.createObjectBuilder().add("items", array).build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Failed to load charges").build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject payload = JsonUtil.readJsonObject(request);
        BusinessCharges charges = new BusinessCharges();
        charges.setPlatformFee(payload.getJsonNumber("platform_fee").bigDecimalValue());
        charges.setDeliveryFee(payload.getJsonNumber("delivery_fee").bigDecimalValue());
        charges.setTaxesPercent(payload.getJsonNumber("taxes_percent").bigDecimalValue());
        try {
            int id = chargesDAO.create(charges);
            JsonUtil.writeJson(response, HttpServletResponse.SC_CREATED,
                Json.createObjectBuilder().add("charges_id", id).build());
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
                Json.createObjectBuilder().add("error", "Missing charges id").build());
            return;
        }
        JsonObject payload = JsonUtil.readJsonObject(request);
        BusinessCharges charges = new BusinessCharges();
        charges.setChargesId(Integer.parseInt(idParam));
        charges.setPlatformFee(payload.getJsonNumber("platform_fee").bigDecimalValue());
        charges.setDeliveryFee(payload.getJsonNumber("delivery_fee").bigDecimalValue());
        charges.setTaxesPercent(payload.getJsonNumber("taxes_percent").bigDecimalValue());
        try {
            chargesDAO.update(charges);
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
                Json.createObjectBuilder().add("error", "Missing charges id").build());
            return;
        }
        try {
            chargesDAO.delete(Integer.parseInt(idParam));
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

    private JsonObject toJson(BusinessCharges charges) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("charges_id", charges.getChargesId());
        builder.add("platform_fee", charges.getPlatformFee());
        builder.add("delivery_fee", charges.getDeliveryFee());
        builder.add("taxes_percent", charges.getTaxesPercent());
        return builder.build();
    }
}
