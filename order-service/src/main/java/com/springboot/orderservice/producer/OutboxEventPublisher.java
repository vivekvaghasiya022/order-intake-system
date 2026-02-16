package com.springboot.orderservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.orderservice.dto.EventStatusEnum;
import com.springboot.orderservice.dto.event.OrderCreated;
import com.springboot.orderservice.model.OutboxEvent;
import com.springboot.orderservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {

    private final KafkaTemplate<String, OrderCreated> kafkaTemplate;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic.order-created}")
    private String topic;

    @Value("${app.kafka.max-retry-attempts:5}")
    private int maxRetryAttempts;

    @Scheduled(fixedDelay = 5000)
    public void publishPendingEvents() {
        List<OutboxEvent> events =
                    outboxRepository.findTop50ByStatusOrderByCreatedAtAsc(EventStatusEnum.PENDING);

        for (OutboxEvent outboxEvent : events) {
           publishSingleEvent(outboxEvent);
        }
    }

    @Transactional
    public void publishSingleEvent(OutboxEvent outboxEvent){
        try {
            OrderCreated orderCreatedEvent = objectMapper.readValue(outboxEvent.getPayload(), OrderCreated.class);

            kafkaTemplate.send(topic, orderCreatedEvent.eventId(), orderCreatedEvent).get();
            log.info("Event published successfully for eventId: {}", orderCreatedEvent.eventId());
            log.debug("Published event payload: {}", outboxEvent.getPayload());

            outboxEvent.setStatus(EventStatusEnum.PROCESSED);
            outboxEvent.setProcessedAt(LocalDateTime.now());
        }
        catch (Exception e) {
            log.error("Failed to publish Event for eventId: {}, error: {}", outboxEvent.getEventId(), e.getMessage());
            outboxEvent.incrementRetryCount();
            outboxEvent.setLastAttemptAt(LocalDateTime.now());

            if (outboxEvent.getRetryCount() >= maxRetryAttempts) {
                outboxEvent.setStatus(EventStatusEnum.FAILED);
            }
        }
        outboxRepository.save(outboxEvent);
    }
}
