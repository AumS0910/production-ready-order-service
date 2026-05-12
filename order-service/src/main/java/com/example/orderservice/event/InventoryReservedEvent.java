package com.example.orderservice.event;

public class InventoryReservedEvent {

    private String orderId;

    public InventoryReservedEvent() {
    }

    public InventoryReservedEvent(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderId() {
        return orderId;
    }
}
