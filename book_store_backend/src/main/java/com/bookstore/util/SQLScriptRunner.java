package com.bookstore.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class SQLScriptRunner {
    private SQLScriptRunner() {
    }

    public static void runFromClasspath(String resourcePath) {
        String script = loadFromClasspath(resourcePath);
        executeStatements(script);
    }

    private static String loadFromClasspath(String resourcePath) {
        try (InputStream input = SQLScriptRunner.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input == null) {
                throw new IllegalStateException("SQL script not found: " + resourcePath);
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read SQL script: " + resourcePath, ex);
        }
    }

    private static void executeStatements(String script) {
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement()) {
            StringBuilder current = new StringBuilder();
            boolean inBlockComment = false;
            String[] lines = script.split("\n");
            for (String line : lines) {
                String trimmed = line.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                if (trimmed.startsWith("/*")) {
                    inBlockComment = true;
                }
                if (inBlockComment) {
                    if (trimmed.endsWith("*/") || trimmed.contains("*/")) {
                        inBlockComment = false;
                    }
                    continue;
                }
                if (trimmed.startsWith("--") || trimmed.startsWith("#")) {
                    continue;
                }
                if (trimmed.startsWith("DELIMITER")) {
                    continue;
                }
                current.append(line).append('\n');
                if (trimmed.endsWith(";")) {
                    String statement = current.toString().trim();
                    statement = statement.substring(0, statement.length() - 1).trim();
                    if (!statement.isEmpty()) {
                        stmt.execute(statement);
                    }
                    current.setLength(0);
                }
            }
            String remaining = current.toString().trim();
            if (!remaining.isEmpty()) {
                stmt.execute(remaining);
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to execute SQL script", ex);
        }
    }
}
