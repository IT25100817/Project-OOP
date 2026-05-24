package com.example.grocery.model;

public abstract class Product {
    private String id;
    private String name;
    private String category;
    private String brand;
    private double price;
    private int stockQuantity;
    private String status;
    private String imagePath;

    protected Product() {
    }

    protected Product(String id, String name, String category, String brand, double price, int stockQuantity, String status, String imagePath) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.imagePath = imagePath;
    }

    // OOP: Abstraction for product-specific sale validation
    public abstract boolean isValidForSale();

    public abstract String getProductType();

    public abstract String getExpiryDisplay();

    public boolean hasSufficientStock(int requestedQty) {
        return requestedQty > 0 && stockQuantity >= requestedQty;
    }

    public void reduceStock(int qty) {
        this.stockQuantity = Math.max(0, this.stockQuantity - qty);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
// Product Management Module - IT25101026
