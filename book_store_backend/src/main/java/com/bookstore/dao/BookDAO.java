package com.bookstore.dao;

import com.bookstore.model.Book;
import com.bookstore.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BookDAO {
    private final ResourceBundle queries = ResourceBundle.getBundle("queries");

    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("book.getAll"))) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapBook(rs));
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return books;
    }

    public Book getBookById(int id) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("book.getById"))) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBook(rs);
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return null;
    }

    public List<Book> searchBooks(String keyword) throws SQLException {
        List<Book> books = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("book.search"))) {
            String search = "%" + keyword + "%";
            ps.setString(1, search);
            ps.setString(2, search);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapBook(rs));
                }
            }
        } finally {
            DBConnection.release(conn);
        }
        return books;
    }

    public int addBook(Book book) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("book.insert"), Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, book.getBookPhoto());
            ps.setString(2, book.getBookName());
            ps.setString(3, book.getBookCategory());
            ps.setString(4, book.getBookDescription());
            ps.setString(5, book.getAuthorName());
            ps.setString(6, book.getAuthorDescription());
            ps.setBigDecimal(7, book.getPrice());
            ps.setBigDecimal(8, book.getDiscountPercent());
            ps.setBigDecimal(9, book.getFinalSellingPrice());
            ps.setString(10, book.getStockStatus());
            ps.setInt(11, book.getStockAmount());
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

    public void updateBook(Book book) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("book.update"))) {
            ps.setString(1, book.getBookPhoto());
            ps.setString(2, book.getBookName());
            ps.setString(3, book.getBookCategory());
            ps.setString(4, book.getBookDescription());
            ps.setString(5, book.getAuthorName());
            ps.setString(6, book.getAuthorDescription());
            ps.setBigDecimal(7, book.getPrice());
            ps.setBigDecimal(8, book.getDiscountPercent());
            ps.setBigDecimal(9, book.getFinalSellingPrice());
            ps.setString(10, book.getStockStatus());
            ps.setInt(11, book.getStockAmount());
            ps.setInt(12, book.getBookId());
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    public void deleteBook(int id) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("book.delete"))) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    public void updateStock(int bookId, int stockAmount, String stockStatus) throws SQLException {
        Connection conn = DBConnection.getConnection();
        try (PreparedStatement ps = conn.prepareStatement(queries.getString("book.updateStock"))) {
            ps.setInt(1, stockAmount);
            ps.setString(2, stockStatus);
            ps.setInt(3, bookId);
            ps.executeUpdate();
        } finally {
            DBConnection.release(conn);
        }
    }

    private Book mapBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setBookId(rs.getInt("book_id"));
        book.setBookPhoto(rs.getString("book_photo"));
        book.setBookName(rs.getString("book_name"));
        book.setBookCategory(rs.getString("book_category"));
        book.setBookDescription(rs.getString("book_description"));
        book.setAuthorName(rs.getString("author_name"));
        book.setAuthorDescription(rs.getString("author_description"));
        book.setPrice(rs.getBigDecimal("price"));
        book.setDiscountPercent(rs.getBigDecimal("discount_percent"));
        book.setFinalSellingPrice(rs.getBigDecimal("final_selling_price"));
        book.setStockStatus(rs.getString("stock_status"));
        book.setStockAmount(rs.getInt("stock_amount"));
        book.setCreatedAt(rs.getTimestamp("created_at"));
        book.setUpdatedAt(rs.getTimestamp("updated_at"));
        return book;
    }
}
