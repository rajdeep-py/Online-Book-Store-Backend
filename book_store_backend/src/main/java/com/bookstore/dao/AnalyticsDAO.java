package com.bookstore.dao;

import com.bookstore.model.Analytics;
import com.bookstore.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AnalyticsDAO {
    private final ResourceBundle queries = ResourceBundle.getBundle("queries");

    public Analytics getStats() throws SQLException {
        Analytics analytics = new Analytics();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("analytics.stats"))) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    analytics.setUserCount(rs.getInt("user_count"));
                    analytics.setBookCount(rs.getInt("book_count"));
                    analytics.setOrderCount(rs.getInt("order_count"));
                    analytics.setRevenue(rs.getBigDecimal("revenue"));
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return analytics;
    }
}
