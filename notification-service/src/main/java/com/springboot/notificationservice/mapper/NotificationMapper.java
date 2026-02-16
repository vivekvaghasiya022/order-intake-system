package com.springboot.notificationservice.mapper;

import com.springboot.notificationservice.dto.NotificationResponse;
import com.springboot.notificationservice.model.Notification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationResponse toResponse(Notification notification);
}
