package com.bookstore;

import java.io.File;

import org.apache.catalina.Context;
import org.apache.catalina.Engine;
import org.apache.catalina.startup.Tomcat;

public class Runner {
    public static void main(String[] args) throws Exception {
        String webappDirLocation = "src/main/webapp/";
        ClassLoader projectClassLoader = Runner.class.getClassLoader();

        Thread.currentThread().setContextClassLoader(projectClassLoader);

        Tomcat tomcat = new Tomcat();
        String port = System.getProperty("port", "8080");
        tomcat.setPort(Integer.parseInt(port));

        // Base directory for temp files
        tomcat.setBaseDir("." + File.separator + "tomcat-base");

        File webappDir = new File(webappDirLocation);
        if (!webappDir.exists()) {
            System.err.println("Webapp directory not found: " + webappDir.getAbsolutePath());
            System.exit(1);
        }

        Context context = tomcat.addWebapp("/book_store_backend", webappDir.getAbsolutePath());
        context.setParentClassLoader(projectClassLoader);
        Engine engine = tomcat.getEngine();
        engine.setParentClassLoader(projectClassLoader);

        System.out.println("Starting embedded Tomcat on port: " + port);
        tomcat.getConnector(); // Initialize the default HTTP connector
        tomcat.start();
        tomcat.getServer().await();
    }
}
