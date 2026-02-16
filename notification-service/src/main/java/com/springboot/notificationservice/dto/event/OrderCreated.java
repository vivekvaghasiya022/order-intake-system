package com.springboot.notificationservice.dto.event;

import java.time.Instant;

public record OrderCreated(
        String eventId,
        Instant occurredAt,
        Long orderId,
        String customerEmail,
        String productCode,
        Integer quantity
) {}