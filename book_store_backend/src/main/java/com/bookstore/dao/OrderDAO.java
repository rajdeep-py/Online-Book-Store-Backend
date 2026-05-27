package com.bookstore.dao;

import com.bookstore.model.Order;
import com.bookstore.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OrderDAO {
    private final ResourceBundle queries = ResourceBundle.getBundle("queries");

    public int create(Order order) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("orders.insert"), Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getCustomerId());
            ps.setString(2, order.getItemsOrderedJson());
            ps.setBigDecimal(3, order.getTotalBillAmount());
            ps.setBigDecimal(4, order.getTaxCharges());
            ps.setBigDecimal(5, order.getPlatformFee());
            ps.setBigDecimal(6, order.getDeliveryFee());
            ps.setString(7, order.getOrderStatus());
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

    public void update(Order order) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("orders.update"))) {
            ps.setInt(1, order.getCustomerId());
            ps.setString(2, order.getItemsOrderedJson());
            ps.setBigDecimal(3, order.getTotalBillAmount());
            ps.setBigDecimal(4, order.getTaxCharges());
            ps.setBigDecimal(5, order.getPlatformFee());
            ps.setBigDecimal(6, order.getDeliveryFee());
            ps.setString(7, order.getOrderStatus());
            ps.setInt(8, order.getOrderId());
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    public Order getById(int orderId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("orders.getById"))) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOrder(rs);
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return null;
    }

    public List<Order> getByCustomer(int customerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("orders.getByCustomer"))) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return orders;
    }

    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("orders.getAll"))) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    orders.add(mapOrder(rs));
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return orders;
    }

    public void delete(int orderId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("orders.delete"))) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setCustomerId(rs.getInt("customer_id"));
        order.setItemsOrderedJson(rs.getString("items_ordered"));
        order.setTotalBillAmount(rs.getBigDecimal("total_bill_amount"));
        order.setTaxCharges(rs.getBigDecimal("tax_charges"));
        order.setPlatformFee(rs.getBigDecimal("platform_fee"));
        order.setDeliveryFee(rs.getBigDecimal("delivery_fee"));
        order.setOrderStatus(rs.getString("order_status"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        order.setUpdatedAt(rs.getTimestamp("updated_at"));
        return order;
    }
}
