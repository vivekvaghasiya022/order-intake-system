package com.springboot.notificationservice.controller;

import com.springboot.notificationservice.dto.NotificationResponse;
import com.springboot.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAllNotifications(@RequestParam(required = false) Long orderId) {

        if (orderId != null) {
            log.info("GET /api/notifications - Fetching notifications for orderId: {}", orderId);
            List<NotificationResponse> notifications = notificationService.getByOrderId(orderId);
            return ResponseEntity.ok(notifications);
        }

        log.info("GET /api/notifications - Fetching all notifications");
        List<NotificationResponse> notifications = notificationService.getAll();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getById(@PathVariable Long id) {
        log.info("GET /api/notifications/{} - Fetching notification with id", id);
        NotificationResponse notification = notificationService.getById(id);
        return ResponseEntity.ok(notification);
    }
}
