package com.example.orderservice.mapper;

import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getOrderId(),
                order.getItemName(),
                order.getQuantity(),
                order.getIdempotencyKey()
        );

    }
}
