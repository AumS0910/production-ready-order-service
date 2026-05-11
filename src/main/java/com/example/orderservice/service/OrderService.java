package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.error.GlobalExceptionHandler;
import com.example.orderservice.event.OrderCreatedEvent;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OutboxEvent;
import com.example.orderservice.repository.OrderJpaRepository;
import com.example.orderservice.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.boot.actuate.autoconfigure.wavefront.WavefrontProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class OrderService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderService.class);

    private final OrderJpaRepository orderRepository;

    private final OutboxEventRepository outboxEventRepository;

    private final ObjectMapper objectMapper;

    private final OrderMapper orderMapper;


    private final Counter orderCreatedCounter;

//    private final OrderSideEffectService sideEffectService;

//    public OrderService(OrderJpaRepository orderRepository, OrderSideEffectService sideEffectService) {
//        this.orderRepository = orderRepository;
//        this.sideEffectService = sideEffectService;
//    }

    public OrderService(OrderJpaRepository orderRepository, OutboxEventRepository outboxEventRepository, OrderMapper orderMapper, MeterRegistry meterRegistry, ObjectMapper objectMapper) {

        this.orderRepository = orderRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.orderMapper = orderMapper;
        this.orderCreatedCounter = meterRegistry.counter("orders.created.count");
        this.objectMapper = objectMapper;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {

        Optional<Order> existing = orderRepository.findByIdempotencyKey(request.getIdempotencyKey());

        if (existing.isPresent()) {
            log.info("Duplicate request detected. Returning existing order. ");
            return orderMapper.toResponse(existing.get());
        }

        log.info("Attempting to create order with: orderId={}, itemName={}, quantity={}, idempotencyKey={}",
                request.getOrderId(),
                request.getItemName(),
                request.getQuantity(),
                request.getIdempotencyKey());

        Order order = new Order(
                request.getOrderId(),
                request.getItemName(),
                request.getQuantity(),
                request.getIdempotencyKey()
        );

        Order saved = orderRepository.save(order);

        try {
            String payload = objectMapper.writeValueAsString(
                    new OrderCreatedEvent(saved.getOrderId())
            );

            OutboxEvent event = new OutboxEvent(
                    saved.getOrderId(),
                    "ORDER_CREATED",
                    payload
            );

            outboxEventRepository.save(event);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize event", e);
        }

        orderCreatedCounter.increment();

        log.info("Order created successfully with: orderId={}, itemName={}, quantity={}",
                saved.getOrderId(),
                saved.getItemName(),
                saved.getQuantity());


        return orderMapper.toResponse(saved);
    }

    @Cacheable(value = "orders", key = "#orderId")
    public OrderResponse getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        log.debug("Fetched order with id={}", orderId);

        return orderMapper.toResponse(order);
    }

    @CacheEvict(value = "orders", key = "#orderId")
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void increaseQuantity(String orderId, int delta) {

        if (delta <= 0) {
            throw new IllegalArgumentException("Delta must be positive");
        }


        log.info("Updating quantity for orderId={} by {}", orderId, delta);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.setQuantity(order.getQuantity() + delta);
    }

    public Page<OrderResponse> getOrders (int page, int size) {
        log.debug("Fetching orders page={} size={}", page, size);

        return orderRepository.findAll(PageRequest.of(page, size))
                .map(orderMapper::toResponse);
    }

    public Page<OrderResponse> searchOrders(String itemName, Pageable pageable) {
        log.debug("Searching orders itemName={}", itemName);

        Page<Order> page = (itemName == null)
                ? orderRepository.findAll(pageable)
                : orderRepository.findByItemName(itemName, pageable);

        return page.map(orderMapper::toResponse);
    }


}


