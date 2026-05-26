package com.bookstore.dao;

import com.bookstore.model.User;
import com.bookstore.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UserDAO {
    private final ResourceBundle queries = ResourceBundle.getBundle("queries");

    public User findByEmail(String email) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("customer.findByEmail"))) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return null;
    }

    public User findById(int id) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("customer.findById"))) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return null;
    }

    public int create(User user) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("customer.insert"), Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getProfilePhoto());
            ps.setString(6, user.getAddress());
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

    public List<User> getAllCustomers() throws SQLException {
        List<User> users = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("customer.getAll"))) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapUser(rs));
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return users;
    }

    public void update(User user) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("customer.update"))) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getProfilePhoto());
            ps.setString(6, user.getAddress());
            ps.setInt(7, user.getCustomerId());
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    public void delete(int customerId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("customer.delete"))) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    public void updateLogin(int customerId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("customer.updateLogin"))) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setCustomerId(rs.getInt("customer_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setProfilePhoto(rs.getString("profile_photo"));
        user.setAddress(rs.getString("address"));
        user.setLastLoginAt(rs.getTimestamp("last_login_at"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        return user;
    }
}
