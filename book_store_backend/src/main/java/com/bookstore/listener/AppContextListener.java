package com.bookstore.listener;

import com.bookstore.util.DBConnection;
import com.bookstore.util.SQLScriptRunner;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Configure Session Cookies for Cross-Origin (CORS) compatibility
        jakarta.servlet.SessionCookieConfig cookieConfig = sce.getServletContext().getSessionCookieConfig();
        cookieConfig.setAttribute("SameSite", "None");
        cookieConfig.setSecure(true);

        try (InputStream input = sce.getServletContext().getResourceAsStream("/WEB-INF/classes/db.properties")) {
            Properties props = new Properties();
            if (input != null) {
                props.load(input);
                DBConnection.init(props);
            } else {
                DBConnection.initFromClasspath();
            }
            SQLScriptRunner.runFromClasspath("schema.sql");
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to initialize DBConnection", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        DBConnection.shutdown();
    }
}
