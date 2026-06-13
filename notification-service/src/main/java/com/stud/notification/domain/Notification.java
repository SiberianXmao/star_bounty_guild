package com.stud.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "notifications", schema = "bounty")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(name = "event_type", nullable = false, length = 150)
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 80)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_type", nullable = false, length = 80)
    private NotificationRecipientType recipientType;

    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "message", nullable = false, columnDefinition = "text")
    private String message;

    @Column(name = "related_order_id")
    private UUID relatedOrderId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "consumed_at", nullable = false)
    private Instant consumedAt = Instant.now();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}
