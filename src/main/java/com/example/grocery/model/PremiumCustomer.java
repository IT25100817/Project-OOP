package com.example.grocery.model;

public class PremiumCustomer extends Customer {

    public PremiumCustomer() {
        super();
    }

    public PremiumCustomer(String id, String username, String password, String fullName, String email,
                           String phone, String address, int loyaltyPoints) {
        super(id, username, password, fullName, email, phone, address, UserRole.CUSTOMER, loyaltyPoints);
    }

    @Override
    public double calculateDiscount(double subtotal) {
        // OOP: Polymorphism - premium customer overrides discount calculation
        return subtotal * 0.10;
    }

    @Override
    public String getCustomerType() {
        return "PREMIUM";
    }

    @Override
    public int calculateLoyaltyReward(double orderTotal) {
        return (int) Math.floor(orderTotal / 5.0);
    }
}
