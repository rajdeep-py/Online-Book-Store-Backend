# 📚 Online Bookstore Backend

A enterprise-grade Java 17 backend built using **Jakarta Servlets 6.0**, **JDBC**, **MySQL 8.0**, **Maven**, and designed to deploy on an **Apache Tomcat 11** application server. This project implements high-performance REST APIs for user catalogs, customer shopping carts, administration modules, dynamic platform charge rates, order processing, and fully automated inventory updates.

---

## 📂 Project Structure & Architecture

```
book_store_backend/
├── .github/                   # CI/CD Workflows
├── database/                  # SQL Database Scripts
│   ├── schema.sql             # Creates tables, keys, and indexes automatically
│   ├── sample-data.sql        # Seeds the database with testing data (admin, books, charges)
│   └── triggers.sql           # Database triggers
├── logs/                      # Project runtime logging output directory
├── pom.xml                    # Maven configuration containing all dependencies (Jakarta Servlet, Parsson, MySQL)
├── README.md                  # Comprehensive Documentation
├── uploads/                   # Local file storage root for uploaded media
│   ├── book/                  # Dynamic book cover photos: /uploads/book/{id}/{name}.{ext}
│   └── customers/             # Dynamic customer avatars: /uploads/customers/{id}/{name}.{ext}
└── src/
    └── main/
        ├── java/              # Clean Architecture Source Directory
        │   └── com/
        │       └── bookstore/
        │           ├── controller/           # REST Endpoints / Servlets
        │           │   ├── admin/            # Administrative APIs
        │           │   │   ├── AboutUsServlet.java
        │           │   │   ├── AdminAnalyticsServlet.java
        │           │   │   ├── AdminBookServlet.java
        │           │   │   ├── AdminCustomerServlet.java
        │           │   │   ├── AdminDashboardServlet.java
        │           │   │   ├── AdminOrderServlet.java
        │           │   │   ├── AdminUserServlet.java
        │           │   │   └── BusinessChargesServlet.java
        │           │   ├── auth/             # Session Authentication APIs
        │           │   │   ├── AdminLoginServlet.java
        │           │   │   ├── LoginServlet.java
        │           │   │   ├── LogoutServlet.java
        │           │   │   └── RegisterServlet.java
        │           │   └── user/             # Customer Public Catalog & Transaction APIs
        │           │       ├── BookServlet.java
        │           │       ├── CartServlet.java
        │           │       ├── CheckoutServlet.java
        │           │       ├── ContactMessageServlet.java
        │           │       ├── OrderServlet.java
        │           │       └── SearchServlet.java
        │           ├── dao/                  # Data Access Objects (Raw SQL/JDBC Queries)
        │           │   ├── AboutUsDAO.java
        │           │   ├── AdminDAO.java
        │           │   ├── AnalyticsDAO.java
        │           │   ├── BookDAO.java
        │           │   ├── BusinessChargesDAO.java
        │           │   ├── CartDAO.java
        │           │   ├── CategoryDAO.java
        │           │   ├── ContactMessageDAO.java
        │           │   ├── OrderDAO.java
        │           │   └── UserDAO.java
        │           ├── filter/               # Security Middleware Filters
        │           │   ├── AdminFilter.java  # Admin session check
        │           │   └── AuthFilter.java   # Customer session check
        │           ├── listener/             # Servlet Lifecycle Listeners
        │           │   └── AppContextListener.java
        │           ├── model/                # Data Entities / POJOs
        │           │   ├── AboutUs.java
        │           │   ├── AdminUser.java
        │           │   ├── Analytics.java
        │           │   ├── Book.java
        │           │   ├── BusinessCharges.java
        │           │   ├── Cart.java
        │           │   ├── CartItem.java
        │           │   ├── Category.java
        │           │   ├── ContactMessage.java
        │           │   ├── Order.java
        │           │   ├── OrderItem.java
        │           │   └── User.java
        │           ├── service/              # Core Domain Business Logic Layers
        │           │   ├── AnalyticsService.java
        │           │   ├── AuthService.java  # Authentication helper
        │           │   ├── BookService.java   # Dynamic pricing & stock status calculations
        │           │   ├── CartService.java
        │           │   └── OrderService.java  # Auto-stock decrements & tax/bill calculations
        │           └── util/                 # Technical Helper Utilities
        │               ├── DBConnection.java       # Thread-safe database pooling pool
        │               ├── FileStorageUtil.java    # Photo path generation & writing
        │               ├── JsonUtil.java           # Stream JSON parser helper
        │               ├── PasswordUtil.java       # Salted PBKDF2 password hashing
        │               ├── ResponseUtil.java       # Unified API payload writes
        │               ├── SQLScriptRunner.java    # Automatically parses & runs SQL files
        │               └── SessionUtil.java        # Session attribute helpers
        └── resources/         # Configuration Decoupling Files
            ├── db.properties               # MySQL database connection configurations
            └── queries.properties          # Decoupled SQL queries to avoid inline java statements
```

---

## 💻 Running the Backend Server on macOS

### 🛠️ 1. Prerequisites Setup
Install Java 17, Maven, MySQL, and Tomcat 11 using Homebrew:
```bash
# 1. Install Java Development Kit 17
brew install openjdk@17

# 2. Install Maven (Java Project Builder)
brew install maven

# 3. Install MySQL Server & Tomcat 11
brew install mysql tomcat
```

### 🗄️ 2. MySQL Setup & Initialization
Start the MySQL services and execute the database configuration:
```bash
# 1. Start the MySQL Background Service
brew services start mysql

# 2. Connect to MySQL without a password initially
mysql -u root

# 3. (Inside MySQL Prompt) Setup database, users, and credentials:
mysql> CREATE DATABASE bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
mysql> CREATE USER 'bookstore_user'@'localhost' IDENTIFIED BY 'Bookstore@Secure2026!';
mysql> GRANT ALL PRIVILEGES ON bookstore.* TO 'bookstore_user'@'localhost';
mysql> FLUSH PRIVILEGES;
mysql> exit
```

Run the schema and initial seed data:
```bash
# 1. Initialize tables and constraints
mysql -u bookstore_user -p'Bookstore@Secure2026!' < database/schema.sql

# 2. Seed database with initial Admin login (admin@bookstore.com / admin123) and sample books
mysql -u bookstore_user -p'Bookstore@Secure2026!' < database/sample-data.sql
```

### 💻 3. Configure and Package Project
Configure the database credentials inside `/src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/bookstore?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=bookstore_user
db.password=Bookstore@Secure2026!
db.pool.size=10
```

Build the enterprise WAR archive:
```bash
mvn clean package
```

### 🚀 4. Run on Tomcat Server
Deploy the packaged WAR directly to the local Homebrew Tomcat directory:
```bash
# 1. Start Tomcat Server
brew services start tomcat

# 2. Deploy your war file (copies it to Tomcat webapps directory)
cp target/book_store_backend.war /opt/homebrew/Cellar/tomcat/11.0.22/libexec/webapps/

# 3. Tomcat will automatically extract the archive. Test your server:
curl -s http://localhost:8080/book_store_backend/api/books
```

---

## 🪟 Running the Backend Server on Windows

### 🛠️ 1. Prerequisites Setup
1. **Download JDK 17**: Download and install [Eclipse Temurin JDK 17 (LTS)](https://adoptium.net/temurin/releases/?version=17).
2. **Download Maven**: Download [Apache Maven](https://maven.apache.org/download.cgi) zip, extract it to `C:\Program Files\maven`, and add `C:\Program Files\maven\bin` to your system environment `PATH` variable.
3. **Download MySQL**: Install [MySQL Community Server](https://dev.mysql.com/downloads/installer/) via the Windows Installer.
4. **Download Tomcat 11**: Download the "Core 64-bit Windows zip" from the [Apache Tomcat 11 Page](https://tomcat.apache.org/download-11.cgi) and extract it to `C:\tomcat11`.

### 🗄️ 2. MySQL Setup (Windows Command Prompt)
Open your Command Prompt (CMD) as **Administrator**:
```cmd
:: 1. Navigate to MySQL installation bin folder
cd "C:\Program Files\MySQL\MySQL Server 8.0\bin"

:: 2. Connect to MySQL Server (type your root password set during installation)
mysql -u root -p

:: 3. Run queries inside the prompt:
mysql> CREATE DATABASE bookstore CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
mysql> CREATE USER 'bookstore_user'@'localhost' IDENTIFIED BY 'Bookstore@Secure2026!';
mysql> GRANT ALL PRIVILEGES ON bookstore.* TO 'bookstore_user'@'localhost';
mysql> FLUSH PRIVILEGES;
mysql> exit
```

Run schema and data seeding scripts:
```cmd
:: Run these from your project directory (where database/ is located)
mysql -u bookstore_user -p"Bookstore@Secure2026!" bookstore < database/schema.sql
mysql -u bookstore_user -p"Bookstore@Secure2026!" bookstore < database/sample-data.sql
```

### 💻 3. Configure and Package Project
Configure the database credentials inside `/src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/bookstore?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=bookstore_user
db.password=Bookstore@Secure2026!
```

Build the WAR artifact inside your project root:
```cmd
mvn clean package
```

### 🚀 4. Run on Tomcat Server
Deploy the packaged WAR into your Windows Tomcat folder:
```cmd
:: 1. Copy the WAR to Tomcat webapps directory
copy target\book_store_backend.war C:\tomcat11\webapps\

:: 2. Navigate to Tomcat bin directory and start the server
cd C:\tomcat11\bin
startup.bat
```

To stop the server at any point, run:
```cmd
shutdown.bat
```

---

## 🛠️ Developing and Running in VS Code (Universal)

Visual Studio Code provides a streamlined, fully integrated development experience for managing your Tomcat Server and MySQL databases directly.

### 🔌 1. Recommended Extensions
Open VS Code, click the **Extensions** icon (or press `Ctrl+Shift+X`/`Cmd+Shift+X`) and install:
1. **Extension Pack for Java** (by Microsoft) — Provides full Java development support, code completions, and debugging.
2. **Community Server Connector** (by Red Hat) — Integrates Apache Tomcat into VS Code's sidebar so you can start, stop, and deploy with single clicks.
3. **Database Client** (by Weijan Chen) — An excellent visual MySQL client directly inside VS Code.

### ⚙️ 2. Configure Tomcat in VS Code
1. Once **Community Server Connector** is installed, navigate to the **Servers** tab in the VS Code sidebar.
2. Click **Create New Server** -> Select **Apache Tomcat**.
3. Choose the Tomcat installation directory:
   * **macOS (Homebrew)**: `/opt/homebrew/Cellar/tomcat/11.0.22/libexec`
   * **Windows**: `C:\tomcat11`
4. Name the server `Tomcat 11`.
5. Right-click the newly added server and click **Start Server**.

### 📦 3. Build & Hot Deploy inside VS Code
1. Open a terminal inside VS Code (`Ctrl+`` / `Cmd+``) and build:
   ```bash
   mvn clean package
   ```
2. In the **Servers** sidebar panel, right-click `Tomcat 11` and select **Add Deployment**.
3. Browse and select the WAR file: `target/book_store_backend.war`.
4. Tomcat will automatically deploy, and VS Code will hot-reload your classes whenever you make changes and rebuild!

---

## 🧪 Live API Examples (CURL)

Use these examples to test, mock, or query the backend services.

### 1. Public Books Catalog
**Retrieve all books:**
```bash
curl -i -X GET http://localhost:8080/book_store_backend/api/books
```

**Search books by keyword:**
```bash
curl -i -X GET "http://localhost:8080/book_store_backend/api/books?q=Clean"
```

### 2. Admin Login
**Authenticate administrative profile:**
```bash
curl -i -X POST http://localhost:8080/book_store_backend/auth/admin-login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@bookstore.com", "password": "admin123"}'
```

### 3. Customer Actions (Register, Login, & Checkout)
**Register customer profile:**
```bash
curl -i -X POST http://localhost:8080/book_store_backend/auth/register \
  -H "Content-Type: application/json" \
  -d '{"full_name": "James Smith", "email": "james.smith@example.com", "password": "SecurePassword123!", "phone_number": "+1555123456", "address": "123 Main St, New York, NY"}'
```

**Customer Login:**
```bash
curl -i -X POST http://localhost:8080/book_store_backend/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "james.smith@example.com", "password": "SecurePassword123!"}'
```

**Place an Order (Calculates platforms charges, tax fees, totals, and updates book stock in real-time):**
```bash
curl -i -X POST http://localhost:8080/book_store_backend/api/orders \
  -H "Content-Type: application/json" \
  -d '{"customer_id": 1, "items": [{"book_id": 1, "quantity": 2}, {"book_id": 2, "quantity": 1}]}'
```
