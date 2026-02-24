package com.example.orderservice.model;

import jakarta.persistence.*;

@Entity
@Table (name = "orders",
        indexes = {
            @Index(name = "idx_item_name", columnList = "itemName")
        }
)
public class Order {

    @Id
    @Column(name = "order_id")
    private String orderId;

    @Column(name = "item_name")
    private String itemName;

    private int quantity;

    @Column(unique = true)
    private String idempotencyKey;

    @Version
    private Long version;

    public Order(String orderId, String itemName, int quantity, String idempotencyKey) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.idempotencyKey = idempotencyKey;
    }

    public Order() {
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
