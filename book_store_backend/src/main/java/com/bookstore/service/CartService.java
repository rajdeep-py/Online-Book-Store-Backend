package com.bookstore.service;

import com.bookstore.dao.CartDAO;
import com.bookstore.model.Cart;
import java.sql.SQLException;
import java.util.List;

public class CartService {
    private final CartDAO cartDAO = new CartDAO();

    public Cart getById(int cartId) throws SQLException {
        return cartDAO.getById(cartId);
    }

    public Cart getByCustomer(int customerId) throws SQLException {
        return cartDAO.getByCustomer(customerId);
    }

    public List<Cart> getAll() throws SQLException {
        return cartDAO.getAll();
    }

    public int create(Cart cart) throws SQLException {
        return cartDAO.create(cart);
    }

    public void update(Cart cart) throws SQLException {
        cartDAO.update(cart);
    }

    public void delete(int cartId) throws SQLException {
        cartDAO.delete(cartId);
    }
}
