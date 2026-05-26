package com.bookstore.dao;

import com.bookstore.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import com.bookstore.model.Cart;

public class CartDAO {
    private final ResourceBundle queries = ResourceBundle.getBundle("queries");

    public Cart getById(int cartId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("cart.getById"))) {
            ps.setInt(1, cartId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCart(rs);
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return null;
    }

    public Cart getByCustomer(int customerId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("cart.getByCustomer"))) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCart(rs);
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return null;
    }

    public List<Cart> getAll() throws SQLException {
        List<Cart> carts = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("cart.getAll"))) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    carts.add(mapCart(rs));
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return carts;
    }

    public int create(Cart cart) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("cart.insert"), Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, cart.getCustomerId());
            ps.setString(2, cart.getItemsJson());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return 0;
    }

    public void update(Cart cart) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("cart.update"))) {
            ps.setInt(1, cart.getCustomerId());
            ps.setString(2, cart.getItemsJson());
            ps.setInt(3, cart.getCartId());
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    public void delete(int cartId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("cart.delete"))) {
            ps.setInt(1, cartId);
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    private Cart mapCart(ResultSet rs) throws SQLException {
        Cart cart = new Cart();
        cart.setCartId(rs.getInt("cart_id"));
        cart.setCustomerId(rs.getInt("customer_id"));
        cart.setItemsJson(rs.getString("items"));
        cart.setCreatedAt(rs.getTimestamp("created_at"));
        cart.setUpdatedAt(rs.getTimestamp("updated_at"));
        return cart;
    }
}
