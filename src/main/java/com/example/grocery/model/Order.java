package com.example.grocery.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
    private String id;
    private String customerId;
    private String customerName;
    private String orderType;
    private List<OrderItem> items = new ArrayList<>();
    private double subtotal;
    private double discount;
    private double deliveryFee;
    private double total;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private boolean dispatched;

    public Order() {
    }

    public Order(String id, String customerId, String customerName, String orderType, List<OrderItem> items,
                 double subtotal, double discount, double deliveryFee, double total, OrderStatus status,
                 LocalDateTime createdAt, boolean dispatched) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.orderType = orderType;
        if (items != null) {
            this.items = items;
        }
        this.subtotal = subtotal;
        this.discount = discount;
        this.deliveryFee = deliveryFee;
        this.total = total;
        this.status = status;
        this.createdAt = createdAt;
        this.dispatched = dispatched;
    }

    public boolean canCancel() {
        return !dispatched && (status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(double deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDispatched() {
        return dispatched;
    }

    public void setDispatched(boolean dispatched) {
        this.dispatched = dispatched;
    }
}
