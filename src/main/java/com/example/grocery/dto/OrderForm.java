package com.example.grocery.dto;

import jakarta.validation.constraints.NotBlank;

public class OrderForm {
    @NotBlank(message = "Order type is required")
    private String orderType;

    @NotBlank(message = "Payment type is required")
    private String paymentType;

    private String paymentReference;

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }
}
