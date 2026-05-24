package com.example.grocery.model;

import java.time.LocalDate;

public class PerishableProduct extends Product {
    private LocalDate expiryDate;

    public PerishableProduct() {
        super();
    }

    public PerishableProduct(String id, String name, String category, String brand, double price,
                             int stockQuantity, String status, String imagePath, LocalDate expiryDate) {
        super(id, name, category, brand, price, stockQuantity, status, imagePath);
        this.expiryDate = expiryDate;
    }

    @Override
    public boolean isValidForSale() {
        // OOP: Polymorphism - perishable product checks expiry date
        return expiryDate != null && !expiryDate.isBefore(LocalDate.now());
    }

    @Override
    public String getProductType() {
        return "PERISHABLE";
    }

    @Override
    public String getExpiryDisplay() {
        return expiryDate == null ? "N/A" : expiryDate.toString();
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
}

