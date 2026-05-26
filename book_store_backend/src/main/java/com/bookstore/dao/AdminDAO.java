package com.bookstore.dao;

import com.bookstore.model.AdminUser;
import com.bookstore.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdminDAO {
    private final ResourceBundle queries = ResourceBundle.getBundle("queries");

    public AdminUser findByEmail(String email) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("admin.findByEmail"))) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAdmin(rs);
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return null;
    }

    public AdminUser findById(int adminId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("admin.findById"))) {
            ps.setInt(1, adminId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAdmin(rs);
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return null;
    }

    public List<AdminUser> getAll() throws SQLException {
        List<AdminUser> admins = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("admin.getAll"))) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    admins.add(mapAdmin(rs));
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return admins;
    }

    public int create(AdminUser admin) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("admin.insert"), Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, admin.getAdminName());
            ps.setString(2, admin.getAdminEmail());
            ps.setString(3, admin.getAdminPassword());
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

    public void update(AdminUser admin) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("admin.update"))) {
            ps.setString(1, admin.getAdminName());
            ps.setString(2, admin.getAdminEmail());
            ps.setString(3, admin.getAdminPassword());
            ps.setInt(4, admin.getAdminId());
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    public void delete(int adminId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("admin.delete"))) {
            ps.setInt(1, adminId);
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    public void updateLogin(int adminId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("admin.updateLogin"))) {
            ps.setInt(1, adminId);
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    private AdminUser mapAdmin(ResultSet rs) throws SQLException {
        AdminUser admin = new AdminUser();
        admin.setAdminId(rs.getInt("admin_id"));
        admin.setAdminName(rs.getString("admin_name"));
        admin.setAdminEmail(rs.getString("admin_email"));
        admin.setAdminPassword(rs.getString("admin_password"));
        admin.setCreatedAt(rs.getTimestamp("created_at"));
        admin.setUpdatedAt(rs.getTimestamp("updated_at"));
        admin.setLastLoginAt(rs.getTimestamp("last_login_at"));
        return admin;
    }
}
