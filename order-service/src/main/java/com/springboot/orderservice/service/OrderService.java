package com.springboot.orderservice.service;

import com.springboot.orderservice.dto.OrderRequest;
import com.springboot.orderservice.dto.OrderResponse;
import com.springboot.orderservice.exception.OrderNotFoundException;
import com.springboot.orderservice.mapper.OrderMapper;
import com.springboot.orderservice.model.Order;
import com.springboot.orderservice.producer.OrderEventPublisher;
import com.springboot.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        log.info("Creating order for: {}", request.getProductCode());

        // mapping request dto to order entity
        Order order = orderMapper.toEntity(request);

        Order saved = orderRepository.save(order);

        try {
            orderEventPublisher.publishOrderCreatedEvent(saved);
        } catch (Exception e) {
            log.error("Error while publishing event:", e);
            throw new RuntimeException("Failed to publish OrderCreated event");
        }

        log.info("Order created successfully with id: {}", saved.getId());
        return orderMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll()
                .stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        log.info("Fetching order with id: {}", id);
        return orderRepository.findById(id)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }
}
