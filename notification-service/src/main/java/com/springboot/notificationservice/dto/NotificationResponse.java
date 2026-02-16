package com.springboot.notificationservice.dto;

import java.time.Instant;

public record NotificationResponse(
        Long id,
        Long orderId,
        NotificationTypeEnum type,
        Boolean delivered,
        String message,
        Instant createdAt,
        String eventId
) {}
