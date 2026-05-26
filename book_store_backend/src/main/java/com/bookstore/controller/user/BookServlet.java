package com.bookstore.controller.user;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import com.bookstore.util.FileStorageUtil;
import com.bookstore.util.JsonUtil;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

@MultipartConfig
public class BookServlet extends HttpServlet {
    private final BookService bookService = new BookService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = getIdParam(request);
        String search = request.getParameter("q");
        try {
            if (idParam != null) {
                Book book = bookService.getBookById(Integer.parseInt(idParam));
                if (book == null) {
                    JsonUtil.writeJson(response, HttpServletResponse.SC_NOT_FOUND,
                        Json.createObjectBuilder().add("error", "Not found").build());
                    return;
                }
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK, toJson(book));
                return;
            }
            List<Book> books = (search != null && !search.isBlank())
                ? bookService.searchBooks(search.trim())
                : bookService.getAllBooks();
            JsonArrayBuilder array = Json.createArrayBuilder();
            for (Book book : books) {
                array.add(toJson(book));
            }
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                Json.createObjectBuilder().add("items", array).build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Failed to load books").build());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if (request.getContentType() != null && request.getContentType().startsWith("multipart/")) {
                Book book = parseBookFromMultipart(request, false);
                int bookId = bookService.addBook(book);
                Part photo = request.getPart("book_photo");
                if (photo != null && photo.getSize() > 0) {
                    String path = FileStorageUtil.storeBookPhoto(request.getServletContext(),
                        bookId, book.getBookName(), photo);
                    book.setBookId(bookId);
                    book.setBookPhoto(path);
                    bookService.updateBook(book);
                }
                JsonUtil.writeJson(response, HttpServletResponse.SC_CREATED,
                    Json.createObjectBuilder().add("book_id", bookId).build());
                return;
            }
            Book book = parseBook(request, false);
            int bookId = bookService.addBook(book);
            JsonUtil.writeJson(response, HttpServletResponse.SC_CREATED,
                Json.createObjectBuilder().add("book_id", bookId).build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Create failed").build());
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            if (request.getContentType() != null && request.getContentType().startsWith("multipart/")) {
                Book book = parseBookFromMultipart(request, true);
                Part photo = request.getPart("book_photo");
                if (photo != null && photo.getSize() > 0) {
                    String path = FileStorageUtil.storeBookPhoto(request.getServletContext(),
                        book.getBookId(), book.getBookName(), photo);
                    book.setBookPhoto(path);
                }
                bookService.updateBook(book);
                JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                    Json.createObjectBuilder().add("message", "Updated").build());
                return;
            }
            Book book = parseBook(request, true);
            bookService.updateBook(book);
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                Json.createObjectBuilder().add("message", "Updated").build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Update failed").build());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = getIdParam(request);
        if (idParam == null) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_BAD_REQUEST,
                Json.createObjectBuilder().add("error", "Missing book id").build());
            return;
        }
        try {
            bookService.deleteBook(Integer.parseInt(idParam));
            JsonUtil.writeJson(response, HttpServletResponse.SC_OK,
                Json.createObjectBuilder().add("message", "Deleted").build());
        } catch (SQLException ex) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                Json.createObjectBuilder().add("error", "Delete failed").build());
        }
    }

    private Book parseBook(HttpServletRequest request, boolean requireId) throws IOException {
        Book book = new Book();
        String idParam = getIdParam(request);
        if (requireId && idParam != null) {
            book.setBookId(Integer.parseInt(idParam));
        }
        JsonObject payload = JsonUtil.readJsonObject(request);
        book.setBookName(payload.getString("book_name", null));
        book.setBookCategory(payload.getString("book_category", null));
        book.setBookDescription(payload.getString("book_description", null));
        book.setAuthorName(payload.getString("author_name", null));
        book.setAuthorDescription(payload.getString("author_description", null));
        book.setPrice(payload.containsKey("price") ? payload.getJsonNumber("price").bigDecimalValue() : null);
        book.setDiscountPercent(payload.containsKey("discount_percent")
            ? payload.getJsonNumber("discount_percent").bigDecimalValue() : BigDecimal.ZERO);
        book.setStockAmount(payload.containsKey("stock_amount") ? payload.getInt("stock_amount") : 0);
        book.setBookPhoto(payload.getString("book_photo", null));
        return book;
    }

    private Book parseBookFromMultipart(HttpServletRequest request, boolean requireId) {
        Book book = new Book();
        String idParam = getIdParam(request);
        if (requireId && idParam != null) {
            book.setBookId(Integer.parseInt(idParam));
        }
        book.setBookName(request.getParameter("book_name"));
        book.setBookCategory(request.getParameter("book_category"));
        book.setBookDescription(request.getParameter("book_description"));
        book.setAuthorName(request.getParameter("author_name"));
        book.setAuthorDescription(request.getParameter("author_description"));
        book.setPrice(new BigDecimal(request.getParameter("price")));
        book.setDiscountPercent(new BigDecimal(request.getParameter("discount_percent")));
        book.setStockAmount(Integer.parseInt(request.getParameter("stock_amount")));
        return book;
    }

    private String getIdParam(HttpServletRequest request) {
        String path = request.getPathInfo();
        if (path != null && path.length() > 1) {
            return path.substring(1);
        }
        return request.getParameter("id");
    }

    private JsonObject toJson(Book book) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("book_id", book.getBookId());
        builder.add("book_photo", book.getBookPhoto() == null ? "" : book.getBookPhoto());
        builder.add("book_name", book.getBookName());
        builder.add("book_category", book.getBookCategory());
        builder.add("book_description", book.getBookDescription() == null ? "" : book.getBookDescription());
        builder.add("author_name", book.getAuthorName());
        builder.add("author_description", book.getAuthorDescription() == null ? "" : book.getAuthorDescription());
        builder.add("price", book.getPrice() == null ? BigDecimal.ZERO : book.getPrice());
        builder.add("discount_percent", book.getDiscountPercent() == null ? BigDecimal.ZERO : book.getDiscountPercent());
        builder.add("final_selling_price", book.getFinalSellingPrice() == null ? BigDecimal.ZERO : book.getFinalSellingPrice());
        builder.add("stock_status", book.getStockStatus() == null ? "" : book.getStockStatus());
        builder.add("stock_amount", book.getStockAmount());
        return builder.build();
    }
}
