package com.springboot.orderservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.orderservice.dto.EventStatusEnum;
import com.springboot.orderservice.dto.OrderRequest;
import com.springboot.orderservice.dto.event.OrderCreated;
import com.springboot.orderservice.exception.OrderNotFoundException;
import com.springboot.orderservice.mapper.OrderMapper;
import com.springboot.orderservice.model.Order;
import com.springboot.orderservice.model.OutboxEvent;
import com.springboot.orderservice.repository.OrderRepository;
import com.springboot.orderservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final OrderMapper orderMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public Order createOrder(OrderRequest request) {
        log.info("Creating order for: {}", request.getProductCode());

        // Mapping request dto to order entity
        Order order = orderMapper.toEntity(request);

        // Save Order
        Order savedOrder = orderRepository.save(order);

        try {
            // Mapping order entity to OrderCreated event
            OrderCreated event = orderMapper.toEvent(savedOrder);

            String jsonPayload = objectMapper.writeValueAsString(event);

            // Save Outbox Event (Same Transaction)
            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventId(event.eventId())
                    .eventType("ORDER")
                    .payload(jsonPayload)
                    .status(EventStatusEnum.PENDING)
                    .build();

            outboxRepository.save(outboxEvent);
            log.info("Outbox event stored successfully for order id: {} , eventId: {}", savedOrder.getId(), event.eventId());
        } catch (JsonProcessingException e) {
            log.error("JSON mapping failed for order: {}", savedOrder.getId(), e);
            throw new RuntimeException("Could not serialize order event", e);
        }

        log.info("Order created successfully with id: {}", savedOrder.getId());
        return savedOrder;
    }

    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(int page, int size) {
        log.info("Fetching orders - Page: {}, Size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        log.info("Fetching order with id: {}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
