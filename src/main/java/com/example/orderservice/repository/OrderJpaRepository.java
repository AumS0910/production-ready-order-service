package com.example.orderservice.repository;

import com.example.orderservice.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, String> {

    Page<Order> findByItemName(String itemName, Pageable pageable);

    Optional<Order> findByIdempotencyKey(String idempotencyKey);
}
