package com.springboot.orderservice.dto;

import java.time.Instant;

public record OrderResponse(
        Long id,
        String customerEmail,
        String productCode,
        Integer quantity,
        OrderStatusEnum status,
        Instant createdAt
) {}