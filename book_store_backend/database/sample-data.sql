USE bookstore;

INSERT INTO admin_users (admin_name, admin_email, admin_password) VALUES
('Admin', 'admin@bookstore.com', 'REPLACE_WITH_HASHED_PASSWORD');

INSERT INTO business_charges (platform_fee, delivery_fee, taxes_percent) VALUES
(20.00, 40.00, 5.00);

INSERT INTO book_inventory (book_photo, book_name, book_category, book_description, author_name, author_description, price, discount_percent, final_selling_price, stock_status, stock_amount)
VALUES
('clean-code.jpg', 'Clean Code', 'Technology', 'Clean Code principles.', 'Robert C. Martin', 'Software craftsmanship author.', 39.99, 10.00, 35.99, 'IN_STOCK', 50),
('pragmatic.jpg', 'The Pragmatic Programmer', 'Technology', 'Classic software craftsmanship book.', 'Andrew Hunt', 'Co-author and speaker.', 42.00, 5.00, 39.90, 'IN_STOCK', 40);
