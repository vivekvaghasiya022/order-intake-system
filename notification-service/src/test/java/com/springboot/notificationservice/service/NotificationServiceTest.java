package com.springboot.notificationservice.service;

import com.springboot.notificationservice.dto.event.OrderCreated;
import com.springboot.notificationservice.model.Notification;
import com.springboot.notificationservice.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void shouldCreateNotificationSuccessfully() {
        OrderCreated event = new OrderCreated(
                UUID.randomUUID().toString(),
                Instant.now(),
                1L,
                "test@example.com",
                "P123",
                2
        );

        when(notificationRepository.existsByEventId(any()))
                .thenReturn(false);

        notificationService.processOrderCreated(event);

        verify(notificationRepository, times(3))
                .save(any(Notification.class));
    }

    @Test
    void shouldNotCreateDuplicateNotification_whenEventAlreadyProcessed() {
        OrderCreated event = new OrderCreated(
                "event-123",
                Instant.now(),
                1L,
                "test@example.com",
                "P123",
                2
        );

        when(notificationRepository.existsByEventId("event-123"))
                .thenReturn(true);

        notificationService.processOrderCreated(event);

        verify(notificationRepository, never()).save(any());
    }

    @Test
    void shouldMarkNotificationAsFailed_whenExceptionOccurs() {
        OrderCreated event = new OrderCreated(
                "event-123",
                Instant.now(),
                1L,
                "test@example.com",
                "P123",
                2
        );

        when(notificationRepository.existsByEventId(any()))
                .thenReturn(false);

        when(notificationRepository.save(any()))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class,
                () -> notificationService.processOrderCreated(event));
    }

    @Test
    void shouldBeIdempotent_whenSameEventProcessedTwice() {

        OrderCreated event = new OrderCreated(
                "event-123",
                Instant.now(),
                1L,
                "test@example.com",
                "P123",
                2
        );

        when(notificationRepository.existsByEventId("event-123"))
                .thenReturn(true)
                .thenReturn(false);

        notificationService.processOrderCreated(event);
        notificationService.processOrderCreated(event);

        verify(notificationRepository, times(3))
                .save(any());
    }

}
