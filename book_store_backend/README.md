# Online Bookstore Backend

A production-ready Java 17 backend using Jakarta Servlets, JDBC (MySQL), Maven, and Tomcat 11. It provides robust REST APIs for a complete online bookstore application including admin management, user catalogs, shopping carts, business fee configuration, order processing, and dynamic calculations.

---

## 🏛️ Database Architecture & Schema

The backend uses a MySQL 8 database (`bookstore`) with 8 highly structured tables:

1. **Admin Users (`admin_users`)**
   - `admin_id` (INT, Primary Key, Auto-increment)
   - `admin_name` (VARCHAR)
   - `admin_email` (VARCHAR, Unique)
   - `admin_password` (VARCHAR, Hashed)
   - `created_at` / `updated_at` (TIMESTAMP)
   - `last_login_at` (TIMESTAMP)

2. **Book Inventory (`book_inventory`)**
   - `book_id` (INT, Primary Key, Auto-increment)
   - `book_photo` (VARCHAR - file path)
   - `book_name` (VARCHAR)
   - `book_category` (VARCHAR)
   - `book_description` (TEXT)
   - `author_name` (VARCHAR)
   - `author_description` (TEXT)
   - `price` (DECIMAL)
   - `discount_percent` (DECIMAL)
   - `final_selling_price` (DECIMAL - autocalculated)
   - `stock_status` (VARCHAR - `IN_STOCK`/`OUT_OF_STOCK`)
   - `stock_amount` (INT)
   - `created_at` / `updated_at` (TIMESTAMP)

3. **Customer Users (`customer_users`)**
   - `customer_id` (INT, Primary Key, Auto-increment)
   - `full_name` (VARCHAR)
   - `email` (VARCHAR, Unique)
   - `password` (VARCHAR, Hashed)
   - `phone_number` (VARCHAR)
   - `profile_photo` (VARCHAR - file path)
   - `address` (TEXT)
   - `last_login_at` (TIMESTAMP)
   - `created_at` / `updated_at` (TIMESTAMP)

4. **Cart (`cart`)**
   - `cart_id` (INT, Primary Key, Auto-increment)
   - `customer_id` (INT, Foreign Key)
   - `items` (JSON array of cart items: `book_id`, `book_name`, `author_name`, `price`, `discount_percent`, `final_selling_price`, `quantity`)
   - `created_at` / `updated_at` (TIMESTAMP)

5. **Business Charges (`business_charges`)**
   - `charges_id` (INT, Primary Key, Auto-increment)
   - `platform_fee` (DECIMAL)
   - `delivery_fee` (DECIMAL)
   - `taxes_percent` (DECIMAL)
   - `created_at` / `updated_at` (TIMESTAMP)

6. **Orders (`orders`)**
   - `order_id` (INT, Primary Key, Auto-increment)
   - `customer_id` (INT, Foreign Key)
   - `items_ordered` (JSON array of ordered books with snapshots of prices)
   - `total_bill_amount` (DECIMAL - calculated)
   - `tax_charges` (DECIMAL - calculated)
   - `platform_fee` (DECIMAL - fetched from business charges)
   - `delivery_fee` (DECIMAL - fetched from business charges)
   - `order_status` (VARCHAR - e.g., `PLACED`, `SHIPPED`, `DELIVERED`, `CANCELLED`)
   - `created_at` / `updated_at` (TIMESTAMP)

7. **About Us (`about_us`)**
   - `about_id` (INT, Primary Key, Auto-increment)
   - `company_name` (VARCHAR)
   - `company_tagline` (VARCHAR)
   - `company_description` (TEXT)
   - `director_name` (VARCHAR)
   - `director_message` (TEXT)
   - `mission` (TEXT)
   - `vision` (TEXT)
   - `partners` (JSON array of objects with `partner_id`, `partner_name`, `partner_logo`)
   - `phone_no` (VARCHAR)
   - `email_id` (VARCHAR)
   - `address` (TEXT)
   - `created_at` / `updated_at` (TIMESTAMP)

8. **Contact Messages (`contact_messages`)**
   - `message_id` (INT, Primary Key, Auto-increment)
   - `name` (VARCHAR)
   - `email` (VARCHAR)
   - `phone_no` (VARCHAR)
   - `subject` (VARCHAR)
   - `message` (TEXT)
   - `status` (VARCHAR - e.g., `NEW`, `READ`, `RESOLVED`)
   - `created_at` / `updated_at` (TIMESTAMP)

---

## ⚡ Key Feature Highlights

### 1. Dynamic Pricing Calculations
When a book is inserted or updated via `/api/books`, the API automatically calculates the **`final_selling_price`** on the server:
$$\text{final\_selling\_price} = \text{price} \times \left(1 - \frac{\text{discount\_percent}}{100}\right)$$
The server also checks `stock_amount` and automatically assigns the `stock_status` as `IN_STOCK` (if $> 0$) or `OUT_OF_STOCK` (if $\le 0$).

### 2. Automatic Inventory Stock Updates
When an order is successfully placed via `POST /api/orders`:
- The API retrieves the current stock amount for each ordered book.
- Decrements the book's stock by the ordered quantity (capping at a minimum of `0`).
- Dynamically updates the `stock_status` to `OUT_OF_STOCK` if the new stock becomes `0`.

### 3. Business Charges & Total Bill Calculations
The order calculation is completely server-driven:
- The API reads current active rates for `platform_fee`, `delivery_fee`, and `taxes_percent` from the `business_charges` table.
- Subtotal is computed as:
  $$\text{subtotal} = \sum (\text{final\_selling\_price} \times \text{quantity})$$
- Taxes are dynamically calculated:
  $$\text{tax\_charges} = \frac{\text{subtotal} \times \text{taxes\_percent}}{100}$$
- The grand total bill is generated securely on the server side:
  $$\text{total\_bill\_amount} = \text{subtotal} + \text{tax\_charges} + \text{platform\_fee} + \text{delivery\_fee}$$

### 4. Structured File Uploads
Files are stored securely in physical directories under context:
* **Book Covers**: `/uploads/book/{book_id}/{sanitized_book_name}.{ext}`
* **Customer Profile Pictures**: `/uploads/customers/{customer_id}/{sanitized_customer_name}.{ext}`

---

## 🚀 API Endpoints

### 🔐 Authentication Routes
* `POST /auth/login` - Customer login (expects JSON: `{"email": "...", "password": "..."}`)
* `POST /auth/admin-login` - Admin login (expects JSON: `{"email": "...", "password": "..."}`)
* `POST /auth/register` - Customer registration
* `GET /auth/logout` - Clear login sessions

### 📚 Book Catalog APIs (`/api/books/*`)
* `GET /api/books` - Retrieve all books (or search via `?q=keyword`)
* `GET /api/books/{id}` - Retrieve details of a specific book
* `POST /api/books` - Insert a book (handles JSON or multipart/form-data with photo)
* `PUT /api/books/{id}` - Update all fields of a book
* `DELETE /api/books/{id}` - Delete a book

### 🛒 Cart APIs (`/api/carts/*`)
* `GET /api/carts` - Retrieve cart details (uses session or query param `?customer_id=`)
* `POST /api/carts` - Create cart or add items (JSON payload)
* `PUT /api/carts/{id}` - Replace entire cart items list
* `DELETE /api/carts/{id}` - Clear cart

### 📦 Order APIs (`/api/orders/*`)
* `GET /api/orders` - List all orders (filtered by customer session or retrieved as admin)
* `GET /api/orders/{id}` - Get order summary and pricing snapshot
* `POST /api/orders` - Place order (requires JSON: `{"items": [{"book_id": 1, "quantity": 2}, ...]}`)
* `PUT /api/orders/{id}` - Update order status (Admin operation)
* `DELETE /api/orders/{id}` - Cancel/Delete order

### 👥 Customer Management (`/api/customers/*`) (Admin only)
* `GET /api/customers` - List all registered customers
* `GET /api/customers/{id}` - View customer profile details
* `POST /api/customers` - Create customer user
* `PUT /api/customers/{id}` - Update all profile details (handles multipart/form-data for profile picture)
* `DELETE /api/customers/{id}` - Remove customer

### 🛡️ Administrator User Management (`/api/admins/*`) (Admin only)
* `GET /api/admins` - List all administrators
* `GET /api/admins/{id}` - View specific administrator
* `POST /api/admins` - Create admin user
* `PUT /api/admins/{id}` - Edit admin details
* `DELETE /api/admins/{id}` - Revoke admin user

### ⚙️ Business Charges Config (`/api/charges/*`)
* `GET /api/charges` - View list of configurations
* `GET /api/charges/{id}` - Retrieve fee details
* `POST /api/charges` - Create fee rules
* `PUT /api/charges/{id}` - Modify platform, tax, or delivery percentages
* `DELETE /api/charges/{id}` - Reset configurations

### 📄 About Us & Contacts (`/api/about/*` & `/api/contacts/*`)
* `GET | POST | PUT | DELETE /api/about/*` - Manage company profile and info
* `GET | POST | PUT | DELETE /api/contacts/*` - Post public queries or retrieve inquiries (Admin only)

---

## 🛠️ Build and Setup instructions

### Requirements
- **Java 17 JDK**
- **Maven 3.9+**
- **MySQL 8.0+**
- **Apache Tomcat 11**

### Local Database Configuration
1. Initialize the database by creating `bookstore` in MySQL.
2. Edit database access properties under `src/main/resources/db.properties`:
   ```properties
   db.url=jdbc:mysql://localhost:3306/bookstore?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   db.user=your_username
   db.password=your_password
   ```

### Compile & Build WAR
To build the WAR artifact, compile the Java source code and bundle resources:
```bash
mvn clean package
```
Deploy the generated `target/book_store_backend.war` by placing it in your Tomcat 11 `webapps/` folder.
On server startup, the `AppContextListener` will automatically execute `database/schema.sql` to setup tables, constraints, and indexes.
