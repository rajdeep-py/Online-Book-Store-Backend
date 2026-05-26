package com.bookstore.util;

import com.bookstore.model.AdminUser;
import com.bookstore.model.User;
import jakarta.servlet.http.HttpSession;

public final class SessionUtil {
    public static final String SESSION_CUSTOMER_ID = "customerId";
    public static final String SESSION_CUSTOMER_NAME = "customerName";
    public static final String SESSION_CUSTOMER_EMAIL = "customerEmail";
    public static final String SESSION_ADMIN_ID = "adminId";
    public static final String SESSION_ADMIN_NAME = "adminName";
    public static final String SESSION_ADMIN_EMAIL = "adminEmail";

    private SessionUtil() {
    }

    public static void setCustomer(HttpSession session, User user) {
        session.setAttribute(SESSION_CUSTOMER_ID, user.getCustomerId());
        session.setAttribute(SESSION_CUSTOMER_NAME, user.getFullName());
        session.setAttribute(SESSION_CUSTOMER_EMAIL, user.getEmail());
    }

    public static void setAdmin(HttpSession session, AdminUser admin) {
        session.setAttribute(SESSION_ADMIN_ID, admin.getAdminId());
        session.setAttribute(SESSION_ADMIN_NAME, admin.getAdminName());
        session.setAttribute(SESSION_ADMIN_EMAIL, admin.getAdminEmail());
    }

    public static Integer getCustomerId(HttpSession session) {
        Object value = session.getAttribute(SESSION_CUSTOMER_ID);
        return value instanceof Integer ? (Integer) value : null;
    }

    public static Integer getAdminId(HttpSession session) {
        Object value = session.getAttribute(SESSION_ADMIN_ID);
        return value instanceof Integer ? (Integer) value : null;
    }

    public static boolean isCustomerLoggedIn(HttpSession session) {
        return getCustomerId(session) != null;
    }

    public static boolean isAdminLoggedIn(HttpSession session) {
        return getAdminId(session) != null;
    }
}
