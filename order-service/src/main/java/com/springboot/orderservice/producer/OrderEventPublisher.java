package com.springboot.orderservice.producer;

import com.springboot.orderservice.dto.event.OrderCreated;
import com.springboot.orderservice.mapper.OrderMapper;
import com.springboot.orderservice.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, OrderCreated> kafkaTemplate;
    private final OrderMapper orderMapper;

    @Value("${app.kafka.topic.order-created}")
    private String topic;

    public void publishOrderCreatedEvent(Order order) {
        try {
            // mapping entity to OrderCreated event
            OrderCreated orderCreatedEvent = orderMapper.toEvent(order);

            kafkaTemplate.send(topic, orderCreatedEvent.eventId(), orderCreatedEvent).get();
            log.info("OrderCreated event published successfully for order id: {} , eventId: {}", order.getId(), orderCreatedEvent.eventId());
            log.debug("Published event payload: {}", orderCreatedEvent);

        }
        catch (Exception e) {
            log.error("Failed to publish OrderCreated event for order id: {}, error: {}", order.getId(), e.getMessage());
            throw new RuntimeException("Failed to publish OrderCreated event", e);
        }
    }
}
