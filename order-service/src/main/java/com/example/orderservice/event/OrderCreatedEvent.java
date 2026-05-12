package com.example.orderservice.event;

public class OrderCreatedEvent {

    private String orderId;

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    private String itemName;
    private int quantity;


    public OrderCreatedEvent(String orderId, String itemName, int quantity) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.quantity = quantity;
    }

    public String getOrderId() {
        return orderId;
    }
}
