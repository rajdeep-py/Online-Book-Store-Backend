package com.bookstore.util;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileStorageUtil {
    private static final String UPLOADS_BASE_DIR = System.getProperty("user.home") + File.separator + "bookstore_uploads";

    private FileStorageUtil() {
    }

    public static String storeBookPhoto(ServletContext context, int bookId, String bookName, Part filePart)
            throws IOException {
        String extension = getExtension(filePart);
        String safeName = sanitizeName(bookName);
        String relativeDir = "/uploads/book/" + bookId;
        String relativePath = relativeDir + "/" + safeName + extension;
        storeFile(relativeDir, relativePath, filePart);
        return relativePath;
    }

    public static String storeCustomerPhoto(ServletContext context, int customerId, String fullName, Part filePart)
            throws IOException {
        String extension = getExtension(filePart);
        String safeName = sanitizeName(fullName);
        String relativeDir = "/uploads/customers/" + customerId;
        String relativePath = relativeDir + "/" + safeName + extension;
        storeFile(relativeDir, relativePath, filePart);
        return relativePath;
    }

    private static void storeFile(String relativeDir, String relativePath, Part filePart)
            throws IOException {
        String internalDir = relativeDir;
        if (internalDir.startsWith("/uploads")) {
            internalDir = internalDir.substring("/uploads".length());
        }
        String internalPath = relativePath;
        if (internalPath.startsWith("/uploads")) {
            internalPath = internalPath.substring("/uploads".length());
        }

        File dir = new File(UPLOADS_BASE_DIR, internalDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File output = new File(UPLOADS_BASE_DIR, internalPath);
        filePart.write(output.getAbsolutePath());
    }

    private static String getExtension(Part part) {
        String fileName = part.getSubmittedFileName();
        if (fileName == null) {
            return "";
        }
        int dot = fileName.lastIndexOf('.');
        if (dot < 0) {
            return "";
        }
        return fileName.substring(dot);
    }

    private static String sanitizeName(String input) {
        if (input == null) {
            return "file";
        }
        return input.trim().replaceAll("[^A-Za-z0-9_-]", "_");
    }
}
