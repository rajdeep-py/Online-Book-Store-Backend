package com.bookstore.service;

import com.bookstore.dao.AnalyticsDAO;
import com.bookstore.model.Analytics;
import java.sql.SQLException;

public class AnalyticsService {
    private final AnalyticsDAO analyticsDAO = new AnalyticsDAO();

    public Analytics getStats() throws SQLException {
        return analyticsDAO.getStats();
    }
}
