package com.springboot.notificationservice.repository;

import com.springboot.notificationservice.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    boolean existsByEventId(String eventId);

    Optional<List<Notification>> findByOrderId(Long orderId);
}
