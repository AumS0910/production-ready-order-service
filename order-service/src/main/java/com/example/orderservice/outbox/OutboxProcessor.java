package com.example.orderservice.outbox;

import com.example.orderservice.event.OrderCreatedEvent;
import com.example.orderservice.kafka.OrderEventProducer;
import com.example.orderservice.model.OutboxEvent;
import com.example.orderservice.model.OutboxStatus;
import com.example.orderservice.repository.OutboxEventRepository;
import com.example.orderservice.service.OrderSideEffectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class OutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(OutboxProcessor.class);
    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;
    private final OrderSideEffectService orderSideEffectService;
    private final OrderEventProducer orderEventProducer;


    public OutboxProcessor(OutboxEventRepository repository,ObjectMapper objectMapper, OrderSideEffectService orderSideEffectService, OrderEventProducer orderEventProducer) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.orderSideEffectService = orderSideEffectService;
        this.orderEventProducer = orderEventProducer;
    }

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void processOutbox() {

        List<OutboxEvent> events = repository.findByStatusIn(List.of(OutboxStatus.PENDING, OutboxStatus.FAILED));

        if(events.isEmpty()) {
            return;
        }

        for(OutboxEvent event: events) {
            try {

                event.markProcessing();
                repository.save(event);

                if("ORDER_CREATED".equals(event.getEventType())) {

                    OrderCreatedEvent domainEvent =
                            objectMapper.readValue(
                                    event.getPayload(),
                                    OrderCreatedEvent.class
                            );

                    orderEventProducer.sendOrderCreatedEvent(domainEvent).get(10, TimeUnit.SECONDS);
                }
                event.markCompleted();

            } catch (Exception e) {

                event.markFailed();

                if(event.getRetryCount() > 3) {
                    log.info("Eevent permanently failed: " + event.getId());
                }
                e.printStackTrace();
            }
            repository.save(event);
        }
    }
}
