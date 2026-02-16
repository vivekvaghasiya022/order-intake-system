package com.springboot.notificationservice.listener;

import com.springboot.notificationservice.dto.event.OrderCreated;
import com.springboot.notificationservice.service.NotificationService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderCreatedEventListenerTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OrderCreatedEventListener listener;

    @Test
    void shouldCallService_whenEventReceived() {

        OrderCreated event = new OrderCreated(
                UUID.randomUUID().toString(),
                Instant.now(),
                1L,
                "test@example.com",
                "P123",
                2
        );

        ConsumerRecord<String, OrderCreated>
                consumerRecord = new ConsumerRecord<>(
                "order-created-topic",
                0,
                0L,
                event.eventId(),
                event);

        listener.handleOrderCreated(consumerRecord);

        verify(notificationService, times(1))
                .processOrderCreated(event);
    }
}
