package com.springboot.orderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.orderservice.dto.EventStatusEnum;
import com.springboot.orderservice.dto.event.OrderCreated;
import com.springboot.orderservice.model.OutboxEvent;
import com.springboot.orderservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OutboxEventProcessService {

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, OrderCreated> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.kafka.topic.order-created}")
    private String topic;

    @Value("${app.kafka.max-retry-attempts:5}")
    private int maxRetryAttempts;

    @Transactional
    public void processBatch() {

        log.info("Starting outbox batch processing...");

        List<OutboxEvent> events = outboxRepository.findBatchForUpdate();

        for (OutboxEvent event : events) {
            processSingleEvent(event);
        }

        log.info("Finished outbox batch processing.");
    }

    public void processSingleEvent(OutboxEvent outboxEvent){
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
    }
}
