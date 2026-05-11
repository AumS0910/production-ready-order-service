package com.example.orderservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class OrderSideEffectService {

    private static final Logger log = LoggerFactory.getLogger(OrderSideEffectService.class);

    @Async
    public void sendOrderConfirmation(String orderId) {
        log.info("Sending confirmation for orderId={}", orderId);
    }
}
