package com.example.grocery.model;

public class NonPerishableProduct extends Product {

    public NonPerishableProduct() {
        super();
    }

    public NonPerishableProduct(String id, String name, String category, String brand, double price,
                                int stockQuantity, String status, String imagePath) {
        super(id, name, category, brand, price, stockQuantity, status, imagePath);
    }

    @Override
    public boolean isValidForSale() {
        return true;
    }

    @Override
    public String getProductType() {
        return "NON_PERISHABLE";
    }

    @Override
    public String getExpiryDisplay() {
        return "N/A";
    }
}
