package com.springboot.notificationservice.controller;

import com.springboot.notificationservice.dto.NotificationTypeEnum;
import com.springboot.notificationservice.exception.NotificationNotFoundException;
import com.springboot.notificationservice.model.Notification;
import com.springboot.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import static org.mockito.Mockito.when;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void shouldReturnAllNotifications() throws Exception {

        when(notificationService.getAllNotifications(0, 10))
                .thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/notifications"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnNotificationById() throws Exception {

        Notification notification = new Notification(
                 1L,
                1L,
                "test@mail.com",
                NotificationTypeEnum.ORDER_CREATED,
                true,
                "Email sent",
                Instant.now(),
                "event-123"
        );

        when(notificationService.getNotificationById(1L))
                .thenReturn(notification);

        mockMvc.perform(get("/api/v1/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void shouldReturn404_whenNotificationNotFound() throws Exception {

        when(notificationService.getNotificationById(1L))
                .thenThrow(new NotificationNotFoundException(1L));

        mockMvc.perform(get("/api/v1/notifications/1"))
                .andExpect(status().isNotFound());
    }
}
