package com.springboot.notificationservice.controller;

import com.springboot.notificationservice.dto.ApiResponse;
import com.springboot.notificationservice.dto.PagedResponse;
import com.springboot.notificationservice.model.Notification;
import com.springboot.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(params = "!orderId")
    public ResponseEntity<ApiResponse<PagedResponse<Notification>>> getAllNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /api/notifications - Fetching all notifications");
        Page<Notification> notificationPage = notificationService.getAllNotifications(page, size);

        List<Notification> content = notificationPage.getContent();

        PagedResponse<Notification> pagedResponse = PagedResponse.<Notification>builder()
                .content(content)
                .pageNumber(notificationPage.getNumber())
                .pageSize(notificationPage.getSize())
                .totalElements(notificationPage.getTotalElements())
                .totalPages(notificationPage.getTotalPages())
                .build();

        return ResponseEntity.ok(
                ApiResponse.success("Notifications fetched successfully", pagedResponse)
        );
    }

    @GetMapping(params = "orderId")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotificationByOrderId(
            @RequestParam Long orderId) {
        log.info("GET /api/notifications - Fetching notifications for orderId: {}", orderId);
        List<Notification> notifications = notificationService.getNotificationByOrderId(orderId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Notifications fetched successfully for orderId: " + orderId, notifications));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Notification>> getNotificationById(@PathVariable Long id) {
        log.info("GET /api/notifications/{} - Fetching notification with id", id);
        Notification notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Notification fetched successfully with id: " + id, notification)
        );
    }
}
