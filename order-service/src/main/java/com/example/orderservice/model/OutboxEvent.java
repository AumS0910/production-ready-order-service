package com.example.orderservice.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {

    @Id
    private String id;

    private String aggregateId;

    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private boolean processed;

    @Enumerated(EnumType.STRING)
    private OutboxStatus status;

    public int getRetryCount() {
        return retryCount;
    }

    private int retryCount;

    private LocalDateTime createdAt;


    public OutboxEvent(String aggregateId, String eventType, String payload) {
        this.id = UUID.randomUUID().toString();
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.processed = false;
        this.createdAt = LocalDateTime.now();
        this.status = OutboxStatus.PENDING;
        this.retryCount = 0;
    }

    public OutboxEvent() {
    }

    public String getId() {
        return id;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public boolean isProcessed() {
        return processed;
    }

   public void markProcessed() {
        this.processed = true;
   }

   public void markProcessing() {
        this.status = OutboxStatus.PROCESSING;
   }

   public void markCompleted() {
        this.status = OutboxStatus.COMPLETED;
        this.processed = true;
   }

   public void markFailed() {
        this.status = OutboxStatus.FAILED;
        this.retryCount++;
   }
}
