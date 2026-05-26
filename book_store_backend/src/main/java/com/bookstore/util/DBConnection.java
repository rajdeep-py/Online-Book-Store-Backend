package com.bookstore.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DBConnection {
    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static final int DEFAULT_POOL_SIZE = 10;

    private static String url;
    private static String user;
    private static String password;
    private static int poolSize = DEFAULT_POOL_SIZE;
    private static boolean initialized = false;

    private static final Deque<Connection> POOL = new ArrayDeque<>();

    private DBConnection() {
    }

    public static synchronized void init(Properties props) {
        if (initialized) {
            return;
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("MySQL JDBC Driver not found", ex);
        }
        url = props.getProperty("db.url");
        user = props.getProperty("db.user");
        password = props.getProperty("db.password");
        String size = props.getProperty("db.pool.size");
        if (size != null && !size.isBlank()) {
            try {
                poolSize = Integer.parseInt(size.trim());
            } catch (NumberFormatException ex) {
                LOGGER.log(Level.WARNING, "Invalid pool size, using default", ex);
            }
        }
        initialized = true;
    }

    public static void initFromClasspath() {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IllegalStateException("db.properties not found in classpath");
            }
            Properties props = new Properties();
            props.load(input);
            init(props);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load db.properties", ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        if (!initialized) {
            initFromClasspath();
        }
        synchronized (POOL) {
            if (!POOL.isEmpty()) {
                Connection conn = POOL.pop();
                if (conn != null && !conn.isClosed()) {
                    return conn;
                }
            }
        }
        return DriverManager.getConnection(url, user, password);
    }

    public static void release(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            if (conn.isClosed()) {
                return;
            }
            synchronized (POOL) {
                if (POOL.size() < poolSize) {
                    POOL.push(conn);
                } else {
                    conn.close();
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.WARNING, "Failed to release connection", ex);
        }
    }

    public static void shutdown() {
        synchronized (POOL) {
            while (!POOL.isEmpty()) {
                try {
                    POOL.pop().close();
                } catch (SQLException ex) {
                    LOGGER.log(Level.WARNING, "Failed to close connection", ex);
                }
            }
        }
    }
}
