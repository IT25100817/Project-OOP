package com.example.grocery.model;

public class RegularCustomer extends Customer {

    public RegularCustomer() {
        super();
    }

    public RegularCustomer(String id, String username, String password, String fullName, String email,
                           String phone, String address, int loyaltyPoints) {
        super(id, username, password, fullName, email, phone, address, UserRole.CUSTOMER, loyaltyPoints);
    }

    @Override
    public double calculateDiscount(double subtotal) {
        // OOP: Polymorphism - regular customer gets no percentage discount
        return 0.0;
    }

    @Override
    public String getCustomerType() {
        return "REGULAR";
    }

    @Override
    public int calculateLoyaltyReward(double orderTotal) {
        return (int) Math.floor(orderTotal / 10.0);
    }
}
