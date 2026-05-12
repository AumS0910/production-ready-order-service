package com.example.orderservice.integration;

import com.example.orderservice.port.InventoryPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InventoryClient implements InventoryPort {

    private final InventoryFeignClient feignClient;

    public InventoryClient(InventoryFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    private static final Logger log = LoggerFactory.getLogger(InventoryClient.class);

    @Override
    @CircuitBreaker(name = "inventoryService",
    fallbackMethod = "inventoryFallback")
    public void reserveStock(String itemName, int quantity) {

        feignClient.reserveStock(itemName, quantity);
    }

    public void inventoryFallback(String itemName, int quantity, Throwable ex) {
        log.error("Inventory service unavailable. Fallback triggered.");
    }
}
