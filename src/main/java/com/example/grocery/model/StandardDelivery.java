package com.example.grocery.model;

public class StandardDelivery extends Delivery {

    public StandardDelivery() {
        super();
    }

    @Override
    public double calculateDeliveryFee(double subtotal) {
        return subtotal >= 100 ? 0 : 8.0;
    }

    @Override
    public int estimateDeliveryHours() {
        return 24;
    }

    @Override
    public String getDeliveryType() {
        return "STANDARD";
    }
}
