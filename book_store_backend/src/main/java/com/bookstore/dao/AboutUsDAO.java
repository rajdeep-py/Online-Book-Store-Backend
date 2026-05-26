package com.bookstore.dao;

import com.bookstore.model.AboutUs;
import com.bookstore.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AboutUsDAO {
    private final ResourceBundle queries = ResourceBundle.getBundle("queries");

    public List<AboutUs> getAll() throws SQLException {
        List<AboutUs> items = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("about.getAll"))) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(mapAbout(rs));
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return items;
    }

    public AboutUs getById(int aboutId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("about.getById"))) {
            ps.setInt(1, aboutId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAbout(rs);
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return null;
    }

    public int create(AboutUs about) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("about.insert"), Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, about.getCompanyName());
            ps.setString(2, about.getCompanyTagline());
            ps.setString(3, about.getCompanyDescription());
            ps.setString(4, about.getDirectorMessage());
            ps.setString(5, about.getDirectorName());
            ps.setString(6, about.getMission());
            ps.setString(7, about.getVision());
            ps.setString(8, about.getPartnersJson());
            ps.setString(9, about.getPhoneNo());
            ps.setString(10, about.getEmailId());
            ps.setString(11, about.getAddress());
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

    public void update(AboutUs about) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("about.update"))) {
            ps.setString(1, about.getCompanyName());
            ps.setString(2, about.getCompanyTagline());
            ps.setString(3, about.getCompanyDescription());
            ps.setString(4, about.getDirectorMessage());
            ps.setString(5, about.getDirectorName());
            ps.setString(6, about.getMission());
            ps.setString(7, about.getVision());
            ps.setString(8, about.getPartnersJson());
            ps.setString(9, about.getPhoneNo());
            ps.setString(10, about.getEmailId());
            ps.setString(11, about.getAddress());
            ps.setInt(12, about.getAboutId());
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    public void delete(int aboutId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("about.delete"))) {
            ps.setInt(1, aboutId);
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    private AboutUs mapAbout(ResultSet rs) throws SQLException {
        AboutUs about = new AboutUs();
        about.setAboutId(rs.getInt("about_id"));
        about.setCompanyName(rs.getString("company_name"));
        about.setCompanyTagline(rs.getString("company_tagline"));
        about.setCompanyDescription(rs.getString("company_description"));
        about.setDirectorMessage(rs.getString("director_message"));
        about.setDirectorName(rs.getString("director_name"));
        about.setMission(rs.getString("mission"));
        about.setVision(rs.getString("vision"));
        about.setPartnersJson(rs.getString("partners"));
        about.setPhoneNo(rs.getString("phone_no"));
        about.setEmailId(rs.getString("email_id"));
        about.setAddress(rs.getString("address"));
        about.setCreatedAt(rs.getTimestamp("created_at"));
        about.setUpdatedAt(rs.getTimestamp("updated_at"));
        return about;
    }
}
