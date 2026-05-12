package com.example.orderservice.model;

public enum OutboxStatus {

    PENDING,
    PROCESSING,
    FAILED,
    COMPLETED
}
