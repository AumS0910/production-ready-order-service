package com.example.orderservice.repository;

import com.example.orderservice.OrderserviceApplication;
import com.example.orderservice.model.Order;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class InMemoryOrderRepository implements OrderRepository {

    private final Map<String, Order> store = new HashMap<>();

    @Override
    public void save(Order order) {
        store.put(order.getOrderId(), order);
    }

    @Override
    public Order findById(String orderId) {
        return store.get(orderId);
    }
}
