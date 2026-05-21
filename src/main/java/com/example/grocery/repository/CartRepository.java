package com.example.grocery.repository;

import com.example.grocery.model.CartItem;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class CartRepository {

    public CartRepository() {
        FileUtil.ensureBaseFiles();
    }

    public List<CartItem> findAll() {
        List<CartItem> cartItems = new ArrayList<>();
        for (String line : FileUtil.readAllLines(FileUtil.CART_FILE)) {
            CartItem item = parse(line);
            if (item != null) {
                cartItems.add(item);
            }
        }
        return cartItems;
    }

    public List<CartItem> findByCustomerId(String customerId) {
        return findAll().stream()
                .filter(item -> item.getCustomerId().equalsIgnoreCase(customerId))
                .collect(Collectors.toList());
    }

    public String generateCartId() {
        List<String> ids = findAll().stream().map(CartItem::getCartId).collect(Collectors.toList());
        return FileUtil.nextId("CRT", ids);
    }

    public void replaceCustomerCart(String customerId, List<CartItem> newItems) {
        List<CartItem> allItems = findAll();
        allItems.removeIf(item -> item.getCustomerId().equalsIgnoreCase(customerId));
        allItems.addAll(newItems);
        writeAll(allItems);
    }

    public void clearCart(String customerId) {
        List<CartItem> allItems = findAll();
        allItems.removeIf(item -> item.getCustomerId().equalsIgnoreCase(customerId));
        writeAll(allItems);
    }

    private void writeAll(List<CartItem> items) {
        List<String> lines = items.stream().map(this::toLine).collect(Collectors.toList());
        FileUtil.writeAllLines(FileUtil.CART_FILE, lines);
    }

    private CartItem parse(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split("\\|", -1);
        if (parts.length < 7) {
            return null;
        }
        return new CartItem(
                parts[0],
                parts[1],
                parts[2],
                parts[3],
                parts[4],
                FileUtil.parseDouble(parts[5], 0.0),
                FileUtil.parseInt(parts[6], 0)
        );
    }

    private String toLine(CartItem item) {
        return String.join(FileUtil.DELIMITER,
                FileUtil.safe(item.getCartId()),
                FileUtil.safe(item.getCustomerId()),
                FileUtil.safe(item.getCartType()),
                FileUtil.safe(item.getProductId()),
                FileUtil.safe(item.getProductName()),
                String.valueOf(item.getUnitPrice()),
                String.valueOf(item.getQuantity())
        );
    }
}
