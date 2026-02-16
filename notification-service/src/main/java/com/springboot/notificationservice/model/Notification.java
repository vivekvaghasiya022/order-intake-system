package com.springboot.notificationservice.model;

import com.springboot.notificationservice.dto.NotificationTypeEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "type")
    @Builder.Default
    private NotificationTypeEnum type = NotificationTypeEnum.ORDER_CREATED;

    @Column(name = "delivered")
    private Boolean delivered;

    @Column(name = "message")
    private String message;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    @Column(name = "event_id")
    private String eventId;
}