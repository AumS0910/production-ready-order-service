package com.example.orderservice.kafka;

import com.example.orderservice.event.OrderCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderEventProducer {

    private final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, Object>> sendOrderCreatedEvent(OrderCreatedEvent event) {
        CompletableFuture<SendResult<String, Object>> send = kafkaTemplate.send("order-created", event);

        log.info("Order event sent to Kafka: {}", event.getOrderId());
        return send;
    }
}
