package com.example.grocery.model;

import java.util.List;

public class RegisteredCart extends Cart {
    private final double discountRate;

    public RegisteredCart(String customerId, List<CartItem> items, double discountRate) {
        super(customerId, items);
        this.discountRate = discountRate;
    }

    @Override
    public double applyDiscount(double subtotal) {
        return subtotal * discountRate;
    }

    @Override
    public String getCartType() {
        return "REGISTERED";
    }
}
