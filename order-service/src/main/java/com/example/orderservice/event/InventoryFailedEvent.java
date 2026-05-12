package com.example.orderservice.event;

public class InventoryFailedEvent {

    private String orderId;

    private String reason;

    public InventoryFailedEvent() {
    }

    public InventoryFailedEvent(String orderId, String reason) {
        this.orderId = orderId;
        this.reason = reason;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getReason() {
        return reason;
    }
}
