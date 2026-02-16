package com.springboot.notificationservice.utility;

import com.springboot.notificationservice.dto.NotificationTypeEnum;
import com.springboot.notificationservice.dto.event.OrderCreated;
import com.springboot.notificationservice.model.Notification;

public class NotificationUtil {
    private NotificationUtil() {
        // Private constructor to prevent instantiation
    }

    public static Notification prepareNotification(OrderCreated event,
                                            boolean processed,
                                            String message,
                                            NotificationTypeEnum notificationType) {

        return Notification.builder()
                .eventId(event.eventId())
                .orderId(event.orderId())
                .type(notificationType)
                .delivered(processed)
                .message(message)
                .build();
    }
}
