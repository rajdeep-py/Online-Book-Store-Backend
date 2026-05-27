package com.bookstore.controller.user;

import com.bookstore.service.CartService;
import com.bookstore.model.Cart;
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

public class CartServlet extends HttpServlet {
    private final CartService cartService = new CartService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = getIdParam(request);
        String customerId = request.getParameter("customer_id");
        HttpSession session = request.getSession(false);
        Integer sessionCustomerId = session != null ? SessionUtil.getCustomerId(session) : null;
        try {
            if (idParam != null) {
                Cart cart = cartService.getById(Integer.parseInt(idParam));
                if (cart == null) {
                    JsonUtil.writeJson(response, HttpServletResponse.SC_NOT_FOUND,
                        Json.createObjectBuilder().add("error", "Not found").build());
                    return;
                }
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK, toJson(cart));
                return;
            }
            if (sessionCustomerId != null) {
                Cart cart = cartService.getByCustomer(sessionCustomerId);
                if (cart == null) {
                    JsonUtil.writeJson(response, HttpServletResponse.SC_NOT_FOUND,
                        Json.createObjectBuilder().add("error", "Not found").build());
                    return;
                }
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK, toJson(cart));
                return;
            }
            if (customerId != null && !customerId.isBlank()) {
                Cart cart = cartService.getByCustomer(Integer.parseInt(customerId));
                if (cart == null) {
                    JsonUtil.writeJson(response, HttpServletResponse.SC_NOT_FOUND,
                        Json.createObjectBuilder().add("error", "Not found").build());
                    return;
                }
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK, toJson(cart));
                return;
            }
            List<Cart> carts = cartService.getAll();
            JsonArrayBuilder array = Json.createArrayBuilder();
            for (Cart cart : carts) {
                array.add(toJson(cart));
            }
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                Json.createObjectBuilder().add("items", array).build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Failed to load carts").build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        JsonObject payload = JsonUtil.readJsonObject(request);
        Cart cart = new Cart();
        HttpSession session = request.getSession(false);
        Integer sessionCustomerId = session != null ? SessionUtil.getCustomerId(session) : null;
        cart.setCustomerId(sessionCustomerId != null ? sessionCustomerId : payload.getInt("customer_id"));
        cart.setItemsJson(payload.getJsonArray("items").toString());
        try {
            Cart existingCart = cartService.getByCustomer(cart.getCustomerId());
            if (existingCart != null) {
                cart.setCartId(existingCart.getCartId());
                cartService.update(cart);
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                    Json.createObjectBuilder().add("cart_id", cart.getCartId()).build());
            } else {
                int cartId = cartService.create(cart);
                JsonUtil.writeJson(response, HttpServletResponse.SC_CREATED,
                    Json.createObjectBuilder().add("cart_id", cartId).build());
            }
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Create or Update failed").build());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = getIdParam(request);
        if (idParam == null) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_BAD_REQUEST,
                Json.createObjectBuilder().add("error", "Missing cart id").build());
            return;
        }
        JsonObject payload = JsonUtil.readJsonObject(request);
        Cart cart = new Cart();
        cart.setCartId(Integer.parseInt(idParam));
        HttpSession session = request.getSession(false);
        Integer sessionCustomerId = session != null ? SessionUtil.getCustomerId(session) : null;
        cart.setCustomerId(sessionCustomerId != null ? sessionCustomerId : payload.getInt("customer_id"));
        cart.setItemsJson(payload.getJsonArray("items").toString());
        try {
            cartService.update(cart);
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
                Json.createObjectBuilder().add("error", "Missing cart id").build());
            return;
        }
        try {
            cartService.delete(Integer.parseInt(idParam));
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

    private JsonObject toJson(Cart cart) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("cart_id", cart.getCartId());
        builder.add("customer_id", cart.getCustomerId());
        builder.add("items", parseItems(cart.getItemsJson()));
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
