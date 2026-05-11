package com.example.orderservice.repository;

import com.example.orderservice.model.Order;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository {

    void save(Order order);

    Order findById(String orderId);
}
