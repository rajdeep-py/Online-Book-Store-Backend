package com.bookstore.controller.admin;

import com.bookstore.model.Analytics;
import com.bookstore.service.AnalyticsService;
import com.bookstore.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class AdminDashboardServlet extends HttpServlet {
    private final AnalyticsService analyticsService = new AnalyticsService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Analytics analytics = analyticsService.getStats();
            request.setAttribute("analytics", analytics);
            ResponseUtil.forward(request, response, "/views/admin/dashboard.jsp");
        } catch (SQLException ex) {
            request.setAttribute("error", "Failed to load dashboard.");
            ResponseUtil.forward(request, response, "/views/error/error.jsp");
        }
    }
}
