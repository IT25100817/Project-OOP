package com.example.grocery.model;

public class ExpressDelivery extends Delivery {

    public ExpressDelivery() {
        super();
    }

    @Override
    public double calculateDeliveryFee(double subtotal) {
        return 20.0;
    }

    @Override
    public int estimateDeliveryHours() {
        return 6;
    }

    @Override
    public String getDeliveryType() {
        return "EXPRESS";
    }
}
