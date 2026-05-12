package com.example.orderservice.model;

import jakarta.persistence.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

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

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    private int retryCount;

    public OrderStatus getStatus() {
        return status;
    }

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Order(String orderId, String itemName, int quantity, String idempotencyKey) {
        this.orderId = orderId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.idempotencyKey = idempotencyKey;
        this.retryCount = 0;
        this.status = OrderStatus.PENDING;
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

    public void setStatus(OrderStatus status) {
        this.status = status;
    }


    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
