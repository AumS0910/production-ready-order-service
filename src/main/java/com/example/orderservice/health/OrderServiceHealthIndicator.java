package com.example.orderservice.health;

import com.example.orderservice.repository.OrderJpaRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.stereotype.Component;

@Component
public class OrderServiceHealthIndicator implements HealthIndicator {

    private final OrderJpaRepository orderRepository;

    public OrderServiceHealthIndicator(OrderJpaRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Health health() {
        try {
            long count = orderRepository.count();

            return Health.up()
                    .withDetail("orderCount", count)
                    .withDetail("service", "OrderService operational")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }

}
