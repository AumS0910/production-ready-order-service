package com.example.orderservice.integration;

import com.example.orderservice.port.InventoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InventoryClient implements InventoryPort {

    private static final Logger log = LoggerFactory.getLogger(InventoryClient.class);

    public void reserveStock(String orderId) {
        log.info("Reserving stock for orderId={}", orderId);
    }
}
