package com.springboot.orderservice.repository;

import com.springboot.orderservice.dto.EventStatusEnum;
import com.springboot.orderservice.model.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findTop50ByStatusOrderByCreatedAtAsc(EventStatusEnum status);
}
