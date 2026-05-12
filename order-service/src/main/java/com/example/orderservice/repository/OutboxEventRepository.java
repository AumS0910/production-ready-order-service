package com.example.orderservice.repository;

import com.example.orderservice.model.OutboxEvent;
import com.example.orderservice.model.OutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByProcessedFalse();

    List<OutboxEvent> findByStatusIn(List<OutboxStatus> statuses);
}
