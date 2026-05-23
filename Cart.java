package com.example.grocery.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Cart {
    private String customerId;
    private List<CartItem> items = new ArrayList<>();

    protected Cart(String customerId, List<CartItem> items) {
        this.customerId = customerId;
        if (items != null) {
            this.items = items;
        }
    }

    public double calculateSubtotal() {
        return items.stream().mapToDouble(CartItem::getLineTotal).sum();
    }

    // OOP: Abstraction + Polymorphism for cart discount rules
    public abstract double applyDiscount(double subtotal);

    public double calculateTotal(double deliveryFee) {
        double subtotal = calculateSubtotal();
        double discount = applyDiscount(subtotal);
        return Math.max(0.0, subtotal - discount + deliveryFee);
    }

    public abstract String getCartType();

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }
}
