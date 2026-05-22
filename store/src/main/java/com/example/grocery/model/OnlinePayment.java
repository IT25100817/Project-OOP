package com.example.grocery.model;

import java.time.LocalDateTime;


public class OnlinePayment extends Payment {

    public OnlinePayment() {
        super();
    }

    public OnlinePayment(String id, String orderId, String customerId, double amount,
                         PaymentStatus status, String reference, LocalDateTime createdAt) {
        super(id, orderId, customerId, amount, status, reference, createdAt);
    }

    @Override
    public boolean verifyPayment() {
        // OOP: Polymorphism - online payment validation requires a reference id
        return getReference() != null && !getReference().isBlank();
    }

    @Override
    public String generateReceipt() {
        return "Online Receipt -> Payment ID: " + getId() + ", Ref: " + getReference() + ", Amount: " + getAmount();
    }

    @Override
    public String getPaymentType() {
        return "ONLINE";
    }
}
