package com.example.grocery.model;

import java.time.LocalDateTime;

public abstract class Delivery {
    private String id;
    private String orderId;
    private String customerId;
    private String assignedPerson;
    private DeliveryStatus status;
    private double fee;
    private int estimatedHours;
    private LocalDateTime createdAt;

    protected Delivery() {
    }

    protected Delivery(String id, String orderId, String customerId, String assignedPerson,
                       DeliveryStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.orderId = orderId;
        this.customerId = customerId;
        this.assignedPerson = assignedPerson;
        this.status = status;
        this.createdAt = createdAt;
    }

    // OOP: Abstraction for delivery behavior based on delivery type
    public abstract double calculateDeliveryFee(double subtotal);

    public abstract int estimateDeliveryHours();

    public abstract String getDeliveryType();

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

    public String getAssignedPerson() {
        return assignedPerson;
    }

    public void setAssignedPerson(String assignedPerson) {
        this.assignedPerson = assignedPerson;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public void setStatus(DeliveryStatus status) {
        this.status = status;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public int getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(int estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
