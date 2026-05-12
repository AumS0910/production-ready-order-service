package com.example.orderservice.kafka;

import com.example.orderservice.event.InventoryFailedEvent;
import com.example.orderservice.event.InventoryReservedEvent;
import com.example.orderservice.service.OrderService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryConsumer {

    private final OrderService orderService;


    public InventoryConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(
    topics = "inventory-reserved",
    groupId = "order-success-group",
    properties = {
        "spring.json.value.default.type=com.example.orderservice.event.InventoryReservedEvent"
    }
    )
    public void handleSuccess(InventoryReservedEvent event) {
        orderService.markOrderCompleted(event.getOrderId());
    }


    @KafkaListener(
    topics = "inventory-failed",
    groupId = "order-failure-group",
    properties = {
        "spring.json.value.default.type=com.example.orderservice.event.InventoryFailedEvent"
    }
    )
    public void handleFailure(InventoryFailedEvent event) {
        orderService.markOrderFailed(event.getOrderId());
    }

}
