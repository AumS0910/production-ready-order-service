package com.example.orderservice.service;

import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.mapper.OrderMapper;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OutboxEvent;
import com.example.orderservice.repository.OrderJpaRepository;
import com.example.orderservice.repository.OutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    private OrderJpaRepository orderRepository;
    private OrderService orderService;
    private OrderMapper orderMapper;
    private ApplicationEventPublisher eventPublisher;
    private OutboxEventRepository outboxEvent;
    private ObjectMapper objectMapper;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setup() {
        orderRepository = mock(OrderJpaRepository.class);
        orderMapper = mock(OrderMapper.class);
        outboxEvent = mock(OutboxEventRepository.class);
        objectMapper = mock(ObjectMapper.class);
        meterRegistry = mock(MeterRegistry.class);

        orderService = new OrderService(orderRepository, outboxEvent, orderMapper, meterRegistry, objectMapper);
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        OrderRequest request = new OrderRequest("ord-1", "Book", 2, "test-123");

        when(orderMapper.toResponse(any(Order.class)))
                .thenAnswer(invocation -> {
                    Order o = invocation.getArgument(0);
                    return new OrderResponse(
                            o.getOrderId(),
                            o.getItemName(),
                            o.getQuantity(),
                            o.getIdempotencyKey()
                    );
                });

        var response = orderService.createOrder(request);

        assertEquals("ord-1", response.getOrderId());
        assertEquals("Book", response.getItemName());
        assertEquals(2, response.getQuantity());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionIfOrderAlreadyExists() {

        OrderRequest request = new OrderRequest("ord-1", "Book", 2, "test-123");

        when(orderRepository.existsById("ord-1")).thenReturn(true);

        assertThrows(IllegalStateException.class,
                () -> orderService.createOrder(request));

        verify(orderRepository, never()).save(any());
    }

    @Test
    void shouldIncreaseQuantity() {

        Order order = new Order("ord-1", "Book", 2, "test-123" );

        when(orderRepository.findById("ord-1"))
                .thenReturn(java.util.Optional.of(order));

        orderService.increaseQuantity("ord-1", 3);

        assertEquals(5, order.getQuantity());
    }
}
