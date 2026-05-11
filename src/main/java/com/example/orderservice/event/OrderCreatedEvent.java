package com.example.orderservice.event;

public class OrderCreatedEvent {

    private String orderId;


    public OrderCreatedEvent(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
