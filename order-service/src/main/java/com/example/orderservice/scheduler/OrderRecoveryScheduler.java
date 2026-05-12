package com.example.orderservice.scheduler;

import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderStatus;
import com.example.orderservice.repository.OrderJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class OrderRecoveryScheduler {

    private final OrderJpaRepository orderJpaRepository;


    public OrderRecoveryScheduler(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    @Scheduled(fixedRate = 30000)
    public void recoverStaleOrders() {

        LocalDateTime staleTime = LocalDateTime.now().minusMinutes(2);

        List<Order> staleOrders = orderJpaRepository.findByStatusAndUpdatedAtBefore(OrderStatus.PENDING, staleTime);

        if(!staleOrders.isEmpty()) {
            log.warn("Found {} stale pending orders", staleOrders.size());
        }
    }
}
