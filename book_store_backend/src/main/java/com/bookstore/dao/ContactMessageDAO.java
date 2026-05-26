package com.bookstore.dao;

import com.bookstore.model.ContactMessage;
import com.bookstore.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ContactMessageDAO {
    private final ResourceBundle queries = ResourceBundle.getBundle("queries");

    public List<ContactMessage> getAll() throws SQLException {
        List<ContactMessage> messages = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("contact.getAll"))) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    messages.add(mapMessage(rs));
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return messages;
    }

    public ContactMessage getById(int messageId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("contact.getById"))) {
            ps.setInt(1, messageId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapMessage(rs);
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return null;
    }

    public int create(ContactMessage message) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("contact.insert"), Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, message.getName());
            ps.setString(2, message.getEmail());
            ps.setString(3, message.getPhoneNo());
            ps.setString(4, message.getSubject());
            ps.setString(5, message.getMessage());
            ps.setString(6, message.getStatus());
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

    public void update(ContactMessage message) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("contact.update"))) {
            ps.setString(1, message.getName());
            ps.setString(2, message.getEmail());
            ps.setString(3, message.getPhoneNo());
            ps.setString(4, message.getSubject());
            ps.setString(5, message.getMessage());
            ps.setString(6, message.getStatus());
            ps.setInt(7, message.getMessageId());
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    public void delete(int messageId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("contact.delete"))) {
            ps.setInt(1, messageId);
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    private ContactMessage mapMessage(ResultSet rs) throws SQLException {
        ContactMessage message = new ContactMessage();
        message.setMessageId(rs.getInt("message_id"));
        message.setName(rs.getString("name"));
        message.setEmail(rs.getString("email"));
        message.setPhoneNo(rs.getString("phone_no"));
        message.setSubject(rs.getString("subject"));
        message.setMessage(rs.getString("message"));
        message.setStatus(rs.getString("status"));
        message.setCreatedAt(rs.getTimestamp("created_at"));
        message.setUpdatedAt(rs.getTimestamp("updated_at"));
        return message;
    }
}
