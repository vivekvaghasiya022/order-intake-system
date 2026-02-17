package com.springboot.notificationservice.service;

import com.springboot.notificationservice.dto.NotificationTypeEnum;
import com.springboot.notificationservice.dto.event.OrderCreated;
import com.springboot.notificationservice.exception.NotificationNotFoundException;
import com.springboot.notificationservice.model.Notification;
import com.springboot.notificationservice.repository.NotificationRepository;
import com.springboot.notificationservice.utility.NotificationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

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
                // Here we can integrate with actual notification services
                // (e.g., Twilio for SMS, SendGrid for Email, Firebase for FCM)

                notification = NotificationUtil.prepareNotification(event, true,
                        type + " sent successfully",
                        NotificationTypeEnum.ORDER_CREATED);

            } catch (Exception e) {
                log.error("Failed to send {} notification for eventId: {}, error: {}",
                        type, event.eventId(), e.getMessage());

                // Creating a failed notification record
                notification = NotificationUtil.prepareNotification(event, false,
                        type + " failed to send: " + e.getMessage(),
                        NotificationTypeEnum.ORDER_CREATED);
            }

            if (notification != null)
                notificationRepository.save(notification);
        }
    }

    @Transactional(readOnly = true)
    public Page<Notification> getAllNotifications(int page, int size) {
        log.info("Fetching notifications - Page: {}, Size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Notification getNotificationById(Long id) {
        log.info("Fetching notification with id: {}", id);
        return notificationRepository.findById(id)
                .orElseThrow(() -> new NotificationNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationByOrderId(Long orderId) {
        log.info("Fetching notification with orderId: {}", orderId);
        return notificationRepository.findByOrderId(orderId)
                .filter(list -> !list.isEmpty())
                .map(list -> list.stream()
                        .toList())
                .orElseThrow(() -> new NotificationNotFoundException(orderId));
    }
}
