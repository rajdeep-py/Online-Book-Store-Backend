package com.bookstore.service;

import com.bookstore.dao.AdminDAO;
import com.bookstore.dao.UserDAO;
import com.bookstore.model.AdminUser;
import com.bookstore.model.User;
import com.bookstore.util.PasswordUtil;
import com.bookstore.util.ValidationUtil;
import java.sql.SQLException;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();
    private final AdminDAO adminDAO = new AdminDAO();

    public User loginCustomer(String email, String password) throws SQLException {
        if (!ValidationUtil.isEmailValid(email) || !ValidationUtil.isNotBlank(password)) {
            return null;
        }
        User user = userDAO.findByEmail(email.trim());
        if (user != null && PasswordUtil.verifyPassword(password, user.getPassword())) {
            userDAO.updateLogin(user.getCustomerId());
            return user;
        }
        return null;
    }

    public AdminUser loginAdmin(String email, String password) throws SQLException {
        if (!ValidationUtil.isEmailValid(email) || !ValidationUtil.isNotBlank(password)) {
            return null;
        }
        AdminUser admin = adminDAO.findByEmail(email.trim());
        if (admin != null && PasswordUtil.verifyPassword(password, admin.getAdminPassword())) {
            adminDAO.updateLogin(admin.getAdminId());
            return admin;
        }
        return null;
    }

    public int registerCustomer(String fullName, String email, String password, String phoneNumber, String address)
            throws SQLException {
        if (!ValidationUtil.isNotBlank(fullName) || !ValidationUtil.isEmailValid(email)
                || !ValidationUtil.isPasswordStrong(password)) {
            return 0;
        }
        if (userDAO.findByEmail(email.trim()) != null) {
            return 0;
        }
        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(email.trim());
        user.setPassword(PasswordUtil.hashPassword(password));
        user.setPhoneNumber(phoneNumber);
        user.setAddress(address);
        return userDAO.create(user);
    }

    public AdminUser getAdminProfile(int adminId) throws SQLException {
        return adminDAO.findById(adminId);
    }

    public User getCustomerProfile(int customerId) throws SQLException {
        return userDAO.findById(customerId);
    }

    public boolean updateCustomerProfile(int customerId, String fullName, String email, String password, String phoneNumber, String address)
            throws SQLException {
        User user = userDAO.findById(customerId);
        if (user == null) {
            return false;
        }
        if (fullName != null && !fullName.isBlank()) {
            user.setFullName(fullName.trim());
        }
        if (email != null && !email.isBlank()) {
            if (!ValidationUtil.isEmailValid(email)) {
                return false;
            }
            User existing = userDAO.findByEmail(email.trim());
            if (existing != null && existing.getCustomerId() != customerId) {
                return false;
            }
            user.setEmail(email.trim());
        }
        if (password != null && !password.isBlank()) {
            if (!ValidationUtil.isPasswordStrong(password)) {
                return false;
            }
            user.setPassword(PasswordUtil.hashPassword(password));
        }
        if (phoneNumber != null) {
            user.setPhoneNumber(phoneNumber.trim());
        }
        if (address != null) {
            user.setAddress(address.trim());
        }
        userDAO.update(user);
        return true;
    }
}
