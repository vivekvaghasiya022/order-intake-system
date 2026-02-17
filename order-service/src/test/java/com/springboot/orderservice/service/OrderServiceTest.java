package com.springboot.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.orderservice.dto.EventStatusEnum;
import com.springboot.orderservice.dto.OrderRequest;
import com.springboot.orderservice.dto.OrderStatusEnum;
import com.springboot.orderservice.dto.event.OrderCreated;
import com.springboot.orderservice.exception.OrderNotFoundException;
import com.springboot.orderservice.mapper.OrderMapper;
import com.springboot.orderservice.model.Order;
import com.springboot.orderservice.model.OutboxEvent;
import com.springboot.orderservice.repository.OrderRepository;
import com.springboot.orderservice.repository.OutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderRequest requestDto;
    private OrderCreated orderCreatedEvent;
    private OutboxEvent outboxEvent;

    @BeforeEach
    void setUp() {
        requestDto = new OrderRequest("test@mail.com", "P100", 2);

        order = Order.builder()
                .id(1L)
                .customerEmail("test@mail.com")
                .productCode("P100")
                .quantity(2)
                .status(OrderStatusEnum.CREATED)
                .createdAt(Instant.now())
                .build();

        orderCreatedEvent = new OrderCreated(
                "event-123",
                Instant.now(),
                1L,
                "test@mail.com",
                "P100",
                2
        );

        outboxEvent = OutboxEvent.builder()
                .eventId("event-123")
                .eventType("ORDER")
                .payload("{}")
                .status(EventStatusEnum.PENDING)
                .build();
    }

    @Test
    void createOrder_shouldPersist() throws JsonProcessingException {
        when(orderMapper.toEntity(requestDto)).thenReturn(order);
        when(orderRepository.save(any())).thenReturn(order);

        when(orderMapper.toEvent(order)).thenReturn(orderCreatedEvent);
        when(outboxRepository.save(any())).thenReturn(outboxEvent);
        when(objectMapper.writeValueAsString(orderCreatedEvent)).thenReturn("{}");

        Order result = orderService.createOrder(requestDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());

        verify(orderRepository).save(any());
        verify(outboxRepository).save(any());
    }

    @Test
    void getAllOrders_shouldReturnPaginatedList() {
        int page = 0;
        int size = 10;

        List<Order> orders = List.of(order);
        Page<Order> orderPage = new PageImpl<>(orders);

        when(orderRepository.findAll(any(Pageable.class))).thenReturn(orderPage);

        Page<Order> result = orderService.getAllOrders(page, size);

        assertNotNull(result);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);

        verify(orderRepository).findAll(any(Pageable.class));
    }

    @Test
    void getOrderById_whenExists_shouldReturnOrder() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getOrderById_whenNotFound_shouldThrowException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class,
                () -> orderService.getOrderById(99L));
    }
}
