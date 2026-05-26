package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.dao.BusinessChargesDAO;
import com.bookstore.dao.OrderDAO;
import com.bookstore.model.Book;
import com.bookstore.model.BusinessCharges;
import com.bookstore.model.Order;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private final OrderDAO orderDAO = new OrderDAO();
    private final BookDAO bookDAO = new BookDAO();
    private final BusinessChargesDAO chargesDAO = new BusinessChargesDAO();

    public int placeOrder(int customerId, String itemsJson) throws SQLException {
        JsonArray items = parseItems(itemsJson);
        JsonArrayBuilder normalizedItems = Json.createArrayBuilder();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (JsonValue value : items) {
            if (value.getValueType() != JsonValue.ValueType.OBJECT) {
                continue;
            }
            JsonObject item = value.asJsonObject();
            int bookId = item.getInt("book_id");
            int quantity = item.getInt("quantity");
            Book book = bookDAO.getBookById(bookId);
            if (book == null) {
                continue;
            }
            BigDecimal linePrice = book.getFinalSellingPrice();
            subtotal = subtotal.add(linePrice.multiply(BigDecimal.valueOf(quantity)));

            normalizedItems.add(Json.createObjectBuilder()
                .add("book_id", bookId)
                .add("book_name", book.getBookName())
                .add("author_name", book.getAuthorName())
                .add("price", book.getPrice())
                .add("discount_percent", book.getDiscountPercent())
                .add("final_price", book.getFinalSellingPrice())
                .add("quantity", quantity));

            int newStock = Math.max(0, book.getStockAmount() - quantity);
            String stockStatus = newStock > 0 ? "IN_STOCK" : "OUT_OF_STOCK";
            bookDAO.updateStock(bookId, newStock, stockStatus);
        }

        BusinessCharges charges = getLatestCharges();
        BigDecimal platformFee = charges != null ? charges.getPlatformFee() : BigDecimal.ZERO;
        BigDecimal deliveryFee = charges != null ? charges.getDeliveryFee() : BigDecimal.ZERO;
        BigDecimal taxPercent = charges != null ? charges.getTaxesPercent() : BigDecimal.ZERO;
        BigDecimal taxCharges = subtotal.multiply(taxPercent)
            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(platformFee).add(deliveryFee).add(taxCharges)
            .setScale(2, RoundingMode.HALF_UP);

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setItemsOrderedJson(normalizedItems.build().toString());
        order.setTotalBillAmount(total);
        order.setTaxCharges(taxCharges);
        order.setPlatformFee(platformFee);
        order.setDeliveryFee(deliveryFee);
        order.setOrderStatus("PLACED");
        return orderDAO.create(order);
    }

    public List<Order> getOrdersByCustomer(int customerId) throws SQLException {
        return orderDAO.getByCustomer(customerId);
    }

    public List<Order> getAllOrders() throws SQLException {
        return orderDAO.getAllOrders();
    }

    public Order getById(int orderId) throws SQLException {
        return orderDAO.getById(orderId);
    }

    public void update(Order order) throws SQLException {
        orderDAO.update(order);
    }

    public void delete(int orderId) throws SQLException {
        orderDAO.delete(orderId);
    }

    private JsonArray parseItems(String itemsJson) {
        try (JsonReader reader = Json.createReader(new StringReader(itemsJson))) {
            return reader.readArray();
        }
    }

    private BusinessCharges getLatestCharges() throws SQLException {
        List<BusinessCharges> charges = chargesDAO.getAll();
        return charges.isEmpty() ? null : charges.get(0);
    }
}
