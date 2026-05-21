package com.example.grocery.model;

public class CartItem {
    private String cartId;
    private String customerId;
    private String cartType;
    private String productId;
    private String productName;
    private double unitPrice;
    private int quantity;

    public CartItem() {
    }

    public CartItem(String cartId, String customerId, String cartType, String productId,
                    String productName, double unitPrice, int quantity) {
        this.cartId = cartId;
        this.customerId = customerId;
        this.cartType = cartType;
        this.productId = productId;
        this.productName = productName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public double getLineTotal() {
        return unitPrice * quantity;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCartType() {
        return cartType;
    }

    public void setCartType(String cartType) {
        this.cartType = cartType;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
