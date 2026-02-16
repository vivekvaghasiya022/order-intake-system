package com.springboot.orderservice.mapper;

import com.springboot.orderservice.dto.OrderRequest;
import com.springboot.orderservice.dto.OrderResponse;
import com.springboot.orderservice.dto.OrderStatusEnum;
import com.springboot.orderservice.dto.event.OrderCreated;
import com.springboot.orderservice.model.Order;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderMapperTest {

    private final OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void shouldMapEntityToResponse() {

        Order order = Order.builder()
                .id(1L)
                .customerEmail("test@mail.com")
                .productCode("P100")
                .quantity(2)
                .status(OrderStatusEnum.CREATED)
                .createdAt(Instant.now())
                .build();

        OrderResponse dto = mapper.toResponse(order);

        assertEquals(order.getId(), dto.id());
        assertEquals(order.getCustomerEmail(), dto.customerEmail());
    }

    @Test
    void shouldMapToEntity() {

        OrderRequest request = new OrderRequest("test@mail.com", "P100", 2);

        Order order = mapper.toEntity(request);

        assertEquals(request.getCustomerEmail(), order.getCustomerEmail());
        assertEquals(request.getProductCode(), order.getProductCode());
        assertEquals(request.getQuantity(), order.getQuantity());

    }

    @Test
    void shouldMapEntityToEvent() {

        Order order = Order.builder()
                .id(1L)
                .customerEmail("test@mail.com")
                .productCode("P100")
                .quantity(2)
                .status(OrderStatusEnum.CREATED)
                .createdAt(Instant.now())
                .build();

        OrderCreated event = mapper.toEvent(order);

        assertEquals(order.getId(), event.orderId());
        assertEquals(order.getCustomerEmail(), event.customerEmail());
    }
}
