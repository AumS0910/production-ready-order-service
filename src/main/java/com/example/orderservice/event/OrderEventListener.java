package com.example.orderservice.event;

import com.example.orderservice.integration.InventoryClient;
import com.example.orderservice.port.InventoryPort;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    private final InventoryPort inventoryPort;

    private final Counter inventoryFailureCount;

    public static volatile int failureCount = 0;
    private static volatile boolean circuitOpen = false;

    private final Set<String> processedOrders = ConcurrentHashMap.newKeySet();

    public OrderEventListener(InventoryPort inventoryPort, MeterRegistry meterRegistry) {
        this.inventoryPort = inventoryPort;
        this.inventoryFailureCount = meterRegistry.counter("inventory.failure.count");

        meterRegistry.gauge("inventory.circuit.open",
                this,
                listener -> circuitOpen ? 1: 0);
    }

    @Async("orderExecutor")
    @EventListener
    @Retryable(
            value = RuntimeException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void handleOrderCreated(OrderCreatedEvent event) {

        log.info("Processing orderId={}", event.getOrderId());

        if(!processedOrders.add(event.getOrderId())) {
            log.warn("Duplicate event detected for orderId={}", event.getOrderId());
            return;
        }


        if (circuitOpen) {
            log.warn("Circuit is OPEN. Skipping inventory call for orderId={}",
                    event.getOrderId());
            return;
        }

        try {


            if (Math.random() > 0.6) {
                throw new RuntimeException("Simulated inventory failure");
            }

            inventoryPort.reserveStock(event.getOrderId());

            failureCount = 0;

            log.info("Inventory reserved successfully for orderId={}",
                    event.getOrderId());

        } catch (RuntimeException ex) {
            inventoryFailureCount.increment();
            failureCount++;
            log.error("Inventory failure. failureCount={}", failureCount);

            if (failureCount >= 3) {
                circuitOpen = true;
                log.error("Circuit OPENED due to repeated failures");
            }

            throw ex; // Let retry handle transient behavior
        }

        log.info("Finished async processing for orderId={} on thread={}",
                event.getOrderId(),
                Thread.currentThread().getName());
    }

}
