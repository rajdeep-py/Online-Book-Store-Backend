package com.bookstore.controller.user;

import com.bookstore.model.Order;
import com.bookstore.service.OrderService;
import com.bookstore.util.JsonUtil;
import com.bookstore.util.SessionUtil;
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
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class OrderServlet extends HttpServlet {
    private final OrderService orderService = new OrderService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = getIdParam(request);
        String customerId = request.getParameter("customer_id");
        HttpSession session = request.getSession(false);
        Integer adminId = session != null ? SessionUtil.getAdminId(session) : null;
        Integer sessionCustomerId = session != null ? SessionUtil.getCustomerId(session) : null;
        try {
            if (idParam != null) {
                Order order = orderService.getById(Integer.parseInt(idParam));
                if (order == null) {
                    JsonUtil.writeJson(response, HttpServletResponse.SC_NOT_FOUND,
                        Json.createObjectBuilder().add("error", "Not found").build());
                    return;
                }
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK, toJson(order));
                return;
            }
            if (sessionCustomerId != null) {
                List<Order> orders = orderService.getOrdersByCustomer(sessionCustomerId);
                JsonArrayBuilder array = Json.createArrayBuilder();
                for (Order order : orders) {
                    array.add(toJson(order));
                }
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                    Json.createObjectBuilder().add("items", array).build());
                return;
            }
            if (adminId == null) {
                JsonUtil.writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                    Json.createObjectBuilder().add("error", "Forbidden").build());
                return;
            }
            if (customerId != null && !customerId.isBlank()) {
                List<Order> orders = orderService.getOrdersByCustomer(Integer.parseInt(customerId));
                JsonArrayBuilder array = Json.createArrayBuilder();
                for (Order order : orders) {
                    array.add(toJson(order));
                }
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                    Json.createObjectBuilder().add("items", array).build());
                return;
            }
            List<Order> orders = orderService.getAllOrders();
            JsonArrayBuilder array = Json.createArrayBuilder();
            for (Order order : orders) {
                array.add(toJson(order));
            }
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                Json.createObjectBuilder().add("items", array).build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Failed to load orders").build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer sessionCustomerId = session != null ? SessionUtil.getCustomerId(session) : null;
        if (sessionCustomerId == null) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                Json.createObjectBuilder().add("error", "Customer login required").build());
            return;
        }
        JsonObject payload = JsonUtil.readJsonObject(request);
        int customerId = sessionCustomerId;
        String itemsJson = payload.getJsonArray("items").toString();
        try {
            int orderId = orderService.placeOrder(customerId, itemsJson);
            JsonUtil.writeJson(response, HttpServletResponse.SC_CREATED,
                Json.createObjectBuilder().add("order_id", orderId).build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Order placement failed").build());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        Integer adminId = session != null ? SessionUtil.getAdminId(session) : null;
        if (adminId == null) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                Json.createObjectBuilder().add("error", "Admin login required").build());
            return;
        }
        String idParam = getIdParam(request);
        if (idParam == null) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_BAD_REQUEST,
                Json.createObjectBuilder().add("error", "Missing order id").build());
            return;
        }
        JsonObject payload = JsonUtil.readJsonObject(request);
        Order order = new Order();
        order.setOrderId(Integer.parseInt(idParam));
        order.setCustomerId(payload.getInt("customer_id"));
        order.setItemsOrderedJson(payload.getJsonArray("items").toString());
        order.setTotalBillAmount(payload.getJsonNumber("total_bill_amount").bigDecimalValue());
        order.setTaxCharges(payload.getJsonNumber("tax_charges").bigDecimalValue());
        order.setPlatformFee(payload.getJsonNumber("platform_fee").bigDecimalValue());
        order.setDeliveryFee(payload.getJsonNumber("delivery_fee").bigDecimalValue());
        order.setOrderStatus(payload.getString("order_status"));
        try {
            orderService.update(order);
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
        HttpSession session = request.getSession(false);
        Integer adminId = session != null ? SessionUtil.getAdminId(session) : null;
        if (adminId == null) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_FORBIDDEN,
                Json.createObjectBuilder().add("error", "Admin login required").build());
            return;
        }
        String idParam = getIdParam(request);
        if (idParam == null) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_BAD_REQUEST,
                Json.createObjectBuilder().add("error", "Missing order id").build());
            return;
        }
        try {
            orderService.delete(Integer.parseInt(idParam));
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

    private JsonObject toJson(Order order) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("order_id", order.getOrderId());
        builder.add("customer_id", order.getCustomerId());
        builder.add("items_ordered", parseItems(order.getItemsOrderedJson()));
        builder.add("total_bill_amount", order.getTotalBillAmount());
        builder.add("tax_charges", order.getTaxCharges());
        builder.add("platform_fee", order.getPlatformFee());
        builder.add("delivery_fee", order.getDeliveryFee());
        builder.add("order_status", order.getOrderStatus());
        return builder.build();
    }

    private JsonArray parseItems(String itemsJson) {
        if (itemsJson == null || itemsJson.isBlank()) {
            return Json.createArrayBuilder().build();
        }
        try (JsonReader reader = Json.createReader(new java.io.StringReader(itemsJson))) {
            return reader.readArray();
        }
    }
}
