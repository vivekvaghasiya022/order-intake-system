package com.springboot.orderservice.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public record OrderCreated(
        String eventId,
        Instant occurredAt,
        Long orderId,
        String customerEmail,
        String productCode,
        Integer quantity
) {}