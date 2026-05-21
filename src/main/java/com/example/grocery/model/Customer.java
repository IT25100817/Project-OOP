package com.example.grocery.model;

public abstract class Customer {
    // OOP: Encapsulation - fields are private and accessed via getters/setters
    private String id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String address;
    private UserRole role;
    private int loyaltyPoints;

    protected Customer() {
    }

    protected Customer(String id, String username, String password, String fullName, String email,
                       String phone, String address, UserRole role, int loyaltyPoints) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.loyaltyPoints = loyaltyPoints;
    }

    // OOP: Abstraction + Polymorphism - subclasses provide discount behavior
    public abstract double calculateDiscount(double subtotal);

    public abstract String getCustomerType();

    public int calculateLoyaltyReward(double orderTotal) {
        return (int) Math.floor(orderTotal / 10.0);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
}
