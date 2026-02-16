package com.springboot.notificationservice.listener;

import com.springboot.notificationservice.dto.event.OrderCreated;
import com.springboot.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCreatedEventListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${app.kafka.topic.order-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void handleOrderCreated(ConsumerRecord<String, OrderCreated> orderCreatedEventConsumerRecord) {
        OrderCreated orderCreated = orderCreatedEventConsumerRecord.value();

        log.info("Received OrderCreated event: {}", orderCreated.eventId());
        log.debug("Event details: {}", orderCreated);

        notificationService.processOrderCreated(orderCreated);
    }
}
