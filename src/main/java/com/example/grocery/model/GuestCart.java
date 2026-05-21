package com.example.grocery.model;

import java.util.List;

public class GuestCart extends Cart {

    public GuestCart(String customerId, List<CartItem> items) {super(customerId, items);
    }

    @Override
    public double applyDiscount(double subtotal) {
        return 0;
    }

    @Override
    public String getCartType() {
        return "GUEST";
    }
}
