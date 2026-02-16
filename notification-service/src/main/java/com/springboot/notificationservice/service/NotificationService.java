package com.springboot.notificationservice.service;

import com.springboot.notificationservice.dto.NotificationResponse;
import com.springboot.notificationservice.dto.NotificationTypeEnum;
import com.springboot.notificationservice.dto.event.OrderCreated;
import com.springboot.notificationservice.exception.NotificationNotFoundException;
import com.springboot.notificationservice.mapper.NotificationMapper;
import com.springboot.notificationservice.model.Notification;
import com.springboot.notificationservice.repository.NotificationRepository;
import com.springboot.notificationservice.utility.NotificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper mapper;

    @Transactional
    public void processOrderCreated(OrderCreated event) {

        // Idempotency check
        if (notificationRepository.existsByEventId(event.eventId())) {
            log.warn("Event already processed for eventId: {}", event.eventId());
            return;
        }

        // Send Notification (SMS, Email, Push Notification)
        String[] notificationTypes = {"SMS", "EMAIL", "FCM"};
        for (String type : notificationTypes) {
            Notification notification;
            try {
                // Simulate sending notification
                log.info("Sending {} notification for eventId: {}", type, event.eventId());
                // Here you would integrate with actual notification services
                // (e.g., Twilio for SMS, SendGrid for Email, Firebase for FCM)

                notification = NotificationUtil.prepareNotification(event, true,
                        type + " sent successfully",
                        NotificationTypeEnum.ORDER_CREATED);

            } catch (Exception e) {
                log.error("Failed to send {} notification for eventId: {}, error: {}",
                        type, event.eventId(), e.getMessage());

                // Optionally, you can create a failed notification record here
                notification = NotificationUtil.prepareNotification(event, false,
                        type + " failed to send: " + e.getMessage(),
                        NotificationTypeEnum.ORDER_CREATED);
            }

            if (notification != null)
                notificationRepository.save(notification);
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getAll() {
        log.info("Fetching all notifications");
        return notificationRepository.findAll()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public NotificationResponse getById(Long id) {
        log.info("Fetching notification with id: {}", id);
        return notificationRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new NotificationNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getByOrderId(Long orderId) {
        log.info("Fetching notification with orderId: {}", orderId);
        return notificationRepository.findByOrderId(orderId)
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream()
                        .map(mapper::toResponse)
                        .toList())
                .orElseThrow(() -> new NotificationNotFoundException(orderId));
    }
}
