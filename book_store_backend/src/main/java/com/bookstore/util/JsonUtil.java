package com.bookstore.util;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static JsonObject readJsonObject(HttpServletRequest request) throws IOException {
        try (InputStream input = request.getInputStream();
             JsonReader reader = Json.createReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            return reader.readObject();
        }
    }

    public static JsonArray readJsonArray(HttpServletRequest request) throws IOException {
        try (InputStream input = request.getInputStream();
             JsonReader reader = Json.createReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            return reader.readArray();
        }
    }

    public static void writeJson(HttpServletResponse response, int status, JsonObject payload) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(payload.toString());
    }
}
