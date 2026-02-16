package com.springboot.notificationservice.exception;

public class NotificationNotFoundException extends RuntimeException{

    public NotificationNotFoundException(Long orderId) {
        super("Notification not found with Id or order id:" + orderId);
    }
}
