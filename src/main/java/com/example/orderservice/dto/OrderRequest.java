package com.example.orderservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class OrderRequest {

    @NotBlank(message = "orderId cannot be empty")
    private String orderId;

    @NotBlank(message = "itemName cannot be empty")
    private String itemName;

    @NotNull
    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    @NotBlank
    private String idempotencyKey;

    public OrderRequest(String orderId, String itemName, Integer quantity, String idempotencyKey) {
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

    public Integer getQuantity() {
        return quantity;
    }
}
