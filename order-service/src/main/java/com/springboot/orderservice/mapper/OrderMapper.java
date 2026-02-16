package com.springboot.orderservice.mapper;

import com.springboot.orderservice.dto.OrderRequest;
import com.springboot.orderservice.dto.OrderResponse;
import com.springboot.orderservice.dto.event.OrderCreated;
import com.springboot.orderservice.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    Order toEntity(OrderRequest request);

    OrderResponse toResponse(Order order);

    @Mapping(target = "eventId", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "occurredAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "orderId", source = "id")
    OrderCreated toEvent(Order order);
}
