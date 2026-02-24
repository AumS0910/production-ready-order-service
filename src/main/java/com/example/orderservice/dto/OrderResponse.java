package com.example.orderservice.dto;

import com.example.orderservice.model.Order;

public class OrderResponse {

    private String orderId;
    private String itemName;
    private int quantity;

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    private String idempotencyKey;


    public OrderResponse(String orderId, String itemName, int quantity, String idempotencyKey) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.idempotencyKey = idempotencyKey;
    }


    public String getOrderId() {
        return orderId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

}
