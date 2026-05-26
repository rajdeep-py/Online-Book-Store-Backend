package com.bookstore.dao;

import com.bookstore.model.Category;
import com.bookstore.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CategoryDAO {
    private final ResourceBundle queries = ResourceBundle.getBundle("queries");

    public List<Category> getAllCategories() throws SQLException {
        List<Category> categories = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("category.getAll"))) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setName(rs.getString("name"));
                    categories.add(category);
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return categories;
    }
}
