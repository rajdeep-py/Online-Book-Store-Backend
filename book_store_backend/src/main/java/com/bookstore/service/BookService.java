package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.model.Book;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

public class BookService {
    private final BookDAO bookDAO = new BookDAO();

    public List<Book> getAllBooks() throws SQLException {
        return bookDAO.getAllBooks();
    }

    public Book getBookById(int id) throws SQLException {
        return bookDAO.getBookById(id);
    }

    public List<Book> searchBooks(String keyword) throws SQLException {
        return bookDAO.searchBooks(keyword);
    }

    public int addBook(Book book) throws SQLException {
        applyPricingAndStock(book);
        return bookDAO.addBook(book);
    }

    public void updateBook(Book book) throws SQLException {
        applyPricingAndStock(book);
        bookDAO.updateBook(book);
    }

    public void deleteBook(int id) throws SQLException {
        bookDAO.deleteBook(id);
    }

    private void applyPricingAndStock(Book book) {
        BigDecimal discount = book.getDiscountPercent() != null ? book.getDiscountPercent() : BigDecimal.ZERO;
        BigDecimal price = book.getPrice() != null ? book.getPrice() : BigDecimal.ZERO;
        BigDecimal multiplier = BigDecimal.valueOf(100).subtract(discount)
            .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        book.setFinalSellingPrice(price.multiply(multiplier).setScale(2, RoundingMode.HALF_UP));
        String stockStatus = book.getStockAmount() > 0 ? "IN_STOCK" : "OUT_OF_STOCK";
        book.setStockStatus(stockStatus);
    }
}
