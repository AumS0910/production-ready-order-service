package com.example.orderservice.service;

public class OrderNotFoundException extends RuntimeException{

    public OrderNotFoundException(String orderId) {
        super("Order not found with id: " + orderId);
    }
}
