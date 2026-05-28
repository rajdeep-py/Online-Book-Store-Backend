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

### 🚀 4. Run & Stop Tomcat Server
Deploy the packaged WAR directly to the local Homebrew Tomcat directory:
```bash
# 1. Start Tomcat Server
brew services start tomcat

# 2. Deploy your war file (copies it to Tomcat webapps directory)
cp target/book_store_backend.war /opt/homebrew/Cellar/tomcat/11.0.22/libexec/webapps/

# 3. Tomcat will automatically extract the archive. Test your server:
curl -s http://localhost:8080/book_store_backend/api/books
```

🛑 **To Stop the Tomcat server on macOS:**
```bash
brew services stop tomcat
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

### 🚀 4. Run & Stop Tomcat Server
Deploy the packaged WAR into your Windows Tomcat folder:
```cmd
:: 1. Copy the WAR to Tomcat webapps directory
copy target\book_store_backend.war C:\tomcat11\webapps\

:: 2. Navigate to Tomcat bin directory and start the server
cd C:\tomcat11\bin
startup.bat
```

🛑 **To Stop the Tomcat server on Windows:**
* **Option A**: Simply close the separate command prompt window that opened when running `startup.bat`.
* **Option B**: Run the shutdown script inside the Tomcat bin folder:
  ```cmd
  cd C:\tomcat11\bin
  shutdown.bat
  ```

---

## 🛠️ Developing & Running inside VS Code

> ⚠️ **IMPORTANT (CAUTION)**: Do **NOT** use VS Code's **"Remote - SSH"** extension to run or add Apache Tomcat! *Remote - SSH* is solely for connecting to external remote servers. Because Apache Tomcat and MySQL are running locally on your own computer, you must run it locally in standard workspace mode.

There are two primary methods to run and deploy the Bookstore project inside Visual Studio Code:

### 💻 Method A: The Simplest Terminal Way (Highly Recommended)
This is the easiest and most direct method. It uses VS Code's integrated terminal to package and hot-deploy your application directly to your active local Tomcat instance.

1. **Open the Project Folder**:
   - Launch VS Code.
   - Click **File ➡️ Open Folder...** and select `/Users/rajdeepdey/Project/Online Book Store/backend/book_store_backend` (or your local project root).
2. **Open the Integrated Terminal**:
   - Open a terminal inside VS Code by pressing ``Ctrl + ` `` or clicking **Terminal ➡️ New Terminal** in the top menu bar.
3. **Build the Project WAR file**:
   - Type this command inside the terminal and press **Enter** to compile and package your Java servlet code:
     ```bash
     mvn clean package
     ```
4. **Deploy the WAR file to Tomcat**:
   * **macOS**:
     ```bash
     cp target/book_store_backend.war /opt/homebrew/Cellar/tomcat/11.0.22/libexec/webapps/
     ```
   * **Windows**:
     ```cmd
     copy target\book_store_backend.war C:\tomcat11\webapps\
     ```
5. **Verify and Play**:
   Tomcat automatically extracts and starts the backend service. Open your browser and navigate to:  
   🔗 **[http://localhost:8080/book_store_backend/api/books](http://localhost:8080/book_store_backend/api/books)**
6. 🛑 **Stop Server**:
   To shut down the background Tomcat server running on your machine:
   * **macOS**: `brew services stop tomcat`
   * **Windows**: Run `shutdown.bat` inside the Tomcat bin folder, or close the terminal window.

---

### ⚙️ Method B: The Visual Sidebar Control Way (Optional UI Tooling)
This method integrates Apache Tomcat directly into your VS Code side panel, allowing you to visually start, stop, and hot-reload deployments with graphical buttons.

#### 🔌 1. Install Extensions
Open the **Extensions** panel inside VS Code (`Ctrl+Shift+X` / `Cmd+Shift+X`) and install:
1. **Extension Pack for Java** (by Microsoft) — Essential for compiling and debugging Java.
2. **Community Server Connector** (by Red Hat) — Integrates Apache Tomcat into your VS Code sidebar.
3. **Database Client** (by Weijan Chen) — An awesome, visual SQL database manager inside VS Code.

#### ⚙️ 2. Configure Tomcat Server in the Sidebar
1. After installing **Community Server Connector**, you will see a new **"SERVERS"** panel at the bottom of your Explorer sidebar.
2. Click the **`+`** (plus) icon on the **SERVERS** header.
3. Select **"No, use custom server..."** in the top dropdown prompt.
4. Select **Apache Tomcat** from the list.
5. Provide the folder path to your local Tomcat:
   * **macOS**: Select the folder `/opt/homebrew/Cellar/tomcat/11.0.22/libexec`
   * **Windows**: Select the folder `C:\tomcat11`
6. Click **Finish**. You will now see `Tomcat 11` listed in the panel.

#### 🚀 3. Run, Stop, and Deploy
1. Build the WAR archive in your terminal:
   ```bash
   mvn clean package
   ```
2. Under the **SERVERS** sidebar panel, **right-click** `Tomcat 11` and select **Start Server**.
3. Once started, **right-click** `Tomcat 11` again, select **Add Deployment**, and pick your packaged WAR file:  
   `target/book_store_backend.war`.
4. Any time you update your Java code, rebuild with `mvn clean package`, and VS Code will dynamically reload your live endpoints!
5. 🛑 **Stop Server**:
   Under the **SERVERS** sidebar panel, **right-click** `Tomcat 11` and select **Stop Server** (or click the red square stop icon in the panel toolbar).

---

## 🧪 Live API Examples (CURL)

Use these examples to test, mock, or query the backend services.

### 1. Admin SignUp Request
**Create an Admin Account:**
curl -i -X POST http://localhost:8080/book_store_backend/auth/admin-login -H "Content-Type: application/json" -d '{"email": "admin@bookstore.com", "password": "admin123"}'



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

### 4. Cart Operations

**Retrieve Customer Cart (Session-based or by query parameter):**
```bash
curl -i -X GET "http://localhost:8080/book_store_backend/api/carts?customer_id=1"
```

**Create / Sync Cart Items:**
```bash
curl -i -X POST http://localhost:8080/book_store_backend/api/carts \
  -H "Content-Type: application/json" \
  -d '{
    "customer_id": 1,
    "items": [
      {"book_id": 1, "quantity": 1},
      {"book_id": 4, "quantity": 2}
    ]
  }'
```

**Update Cart (Requires Cart ID):**
```bash
curl -i -X PUT http://localhost:8080/book_store_backend/api/carts/1 \
  -H "Content-Type: application/json" \
  -d '{
    "customer_id": 1,
    "items": [
      {"book_id": 1, "quantity": 3}
    ]
  }'
```

**Clear / Delete Cart:**
```bash
curl -i -X DELETE http://localhost:8080/book_store_backend/api/carts/1
```

### 5. Order Processing

**Place a New Order (Calculates platform charges, tax fees, totals, and updates book stock in real-time. Requires Customer login session):**
```bash
curl -i -X POST http://localhost:8080/book_store_backend/api/orders \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=YourSessionIdHere" \
  -d '{
    "items": [
      {"book_id": 1, "quantity": 2},
      {"book_id": 2, "quantity": 1}
    ]
  }'
```

**Retrieve Orders (All for Admin, or Customer's own based on session):**
```bash
curl -i -X GET "http://localhost:8080/book_store_backend/api/orders?customer_id=1"
```

**Retrieve a Specific Order (by ID):**
```bash
curl -i -X GET http://localhost:8080/book_store_backend/api/orders/1
```

**Update Order Status / Admin Overrides (Requires Admin login session):**
```bash
curl -i -X PUT http://localhost:8080/book_store_backend/api/orders/1 \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=AdminSessionIdHere" \
  -d '{
    "customer_id": 1,
    "total_bill_amount": 45.99,
    "tax_charges": 2.50,
    "platform_fee": 1.00,
    "delivery_fee": 5.00,
    "order_status": "Shipped",
    "items": [{"book_id": 1, "quantity": 2}]
  }'
```

**Delete an Order (Requires Admin login session):**
```bash
curl -i -X DELETE http://localhost:8080/book_store_backend/api/orders/1 \
  -H "Cookie: JSESSIONID=AdminSessionIdHere"
```
