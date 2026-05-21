package com.example.grocery.model;

import java.time.LocalDateTime;

public abstract class Payment {
    private String id;
    private String orderId;
    private String customerId;
    private double amount;
    private PaymentStatus status;
    private String reference;
    private LocalDateTime createdAt;

    protected Payment() {
    }

    protected Payment(String id, String orderId, String customerId, double amount,
                      PaymentStatus status, String reference, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.status = status;
        this.reference = reference;
        this.createdAt = createdAt;
    }

    // OOP: Abstraction for payment verification by payment method
    public abstract boolean verifyPayment();

    public abstract String generateReceipt();

    public abstract String getPaymentType();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
