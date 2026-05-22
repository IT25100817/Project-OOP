package com.example.grocery.model;

import java.time.LocalDateTime;


public class CashOnDelivery extends Payment {

    public CashOnDelivery() {
        super();
    }

    public CashOnDelivery(String id, String orderId, String customerId, double amount,
                          PaymentStatus status, String reference, LocalDateTime createdAt) {
        super(id, orderId, customerId, amount, status, reference, createdAt);
    }

    @Override
    public boolean verifyPayment() {
        return true;
    }

    @Override
    public String generateReceipt() {
        return "COD Receipt -> Payment ID: " + getId() + ", Amount: " + getAmount();
    }

    @Override
    public String getPaymentType() {
        return "COD";
    }
}
