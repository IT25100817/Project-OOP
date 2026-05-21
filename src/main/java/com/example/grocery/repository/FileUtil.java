package com.example.grocery.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public final class FileUtil {
    public static final String DELIMITER = "|";
    public static final Path DATA_DIR = Paths.get("data");
    public static final Path CUSTOMERS_FILE = DATA_DIR.resolve("customers.txt");
    public static final Path PRODUCTS_FILE = DATA_DIR.resolve("products.txt");
    public static final Path PRODUCT_IMAGES_DIR = DATA_DIR.resolve("product-images");
    public static final Path CART_FILE = DATA_DIR.resolve("cart.txt");
    public static final Path ORDERS_FILE = DATA_DIR.resolve("orders.txt");
    public static final Path DELIVERIES_FILE = DATA_DIR.resolve("deliveries.txt");
    public static final Path PAYMENTS_FILE = DATA_DIR.resolve("payments.txt");
    public static final Path ADMINS_FILE = DATA_DIR.resolve("admins.txt");

    private static final Object LOCK = new Object();

    private FileUtil() {
    }

    public static void ensureBaseFiles() {
        synchronized (LOCK) {
            ensureDirectory(DATA_DIR);
            ensureFile(CUSTOMERS_FILE);
            ensureFile(PRODUCTS_FILE);
            ensureDirectory(PRODUCT_IMAGES_DIR);
            ensureFile(CART_FILE);
            ensureFile(ORDERS_FILE);
            ensureFile(DELIVERIES_FILE);
            ensureFile(PAYMENTS_FILE);
            ensureFile(ADMINS_FILE);
        }
    }

    public static List<String> readAllLines(Path path) {
        synchronized (LOCK) {
            try {
                ensureFile(path);
                return Files.readAllLines(path, StandardCharsets.UTF_8);
            } catch (IOException ex) {
                return new ArrayList<>();
            }
        }
    }

    public static void writeAllLines(Path path, List<String> lines) {
        synchronized (LOCK) {
            try {
                ensureFile(path);
                Files.write(path, lines, StandardCharsets.UTF_8,
                        StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            } catch (IOException ignored) {
            }
        }
    }

    public static void appendLine(Path path, String line) {
        synchronized (LOCK) {
            try {
                ensureFile(path);
                Files.writeString(path, line + System.lineSeparator(), StandardCharsets.UTF_8,
                        StandardOpenOption.APPEND);
            } catch (IOException ignored) {
            }
        }
    }

    public static String safe(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("|", "/").replace(";", ",");
    }

    public static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (Exception ex) {
            return fallback;
        }
    }

    public static double parseDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value);
        } catch (Exception ex) {
            return fallback;
        }
    }

    public static String nextId(String prefix, List<String> existingIds) {
        int max = 0;
        for (String id : existingIds) {
            if (id == null) {
                continue;
            }
            String numeric = id.replaceAll("[^0-9]", "");
            if (!numeric.isBlank()) {
                max = Math.max(max, parseInt(numeric, 0));
            }
        }
        return prefix + String.format("%03d", max + 1);
    }

    private static void ensureDirectory(Path dir) {
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }
        } catch (IOException ignored) {
        }
    }

    public static void ensureFile(Path file) {
        try {
            ensureDirectory(file.getParent());
            if (!Files.exists(file)) {
                Files.createFile(file);
            }
        } catch (IOException ignored) {
        }
    }
}
