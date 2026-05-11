package com.example.orderservice.outbox;

import com.example.orderservice.event.OrderCreatedEvent;
import com.example.orderservice.model.OutboxEvent;
import com.example.orderservice.repository.OutboxEventRepository;
import com.example.orderservice.service.OrderSideEffectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class OutboxProcessor {

    private final OutboxEventRepository repository;
    private final ApplicationEventPublisher publisher;
    private final ObjectMapper objectMapper;
    private final OrderSideEffectService orderSideEffectService;


    public OutboxProcessor(OutboxEventRepository repository, ApplicationEventPublisher publisher, ObjectMapper objectMapper, OrderSideEffectService orderSideEffectService) {
        this.repository = repository;
        this.publisher = publisher;
        this.objectMapper = objectMapper;
        this.orderSideEffectService = orderSideEffectService;
    }

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void processOutbox() {

        List<OutboxEvent> events = repository.findByProcessedFalse();

        if(events.isEmpty()) {
            return;
        }

        for(OutboxEvent event: events) {
            try {

                if("ORDER_CREATED".equals(event.getEventType())) {

                    OrderCreatedEvent domainEvent =
                            objectMapper.readValue(
                                    event.getPayload(),
                                    OrderCreatedEvent.class
                            );

                    publisher.publishEvent(domainEvent);
                    orderSideEffectService.sendOrderConfirmation(domainEvent.getOrderId());
                }

                event.markProcessed();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
