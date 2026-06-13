package com.stud.notification.web.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID eventId,
        String eventType,
        String type,
        String recipientType,
        UUID recipientId,
        String title,
        String message,
        UUID relatedOrderId,
        Instant createdAt
) {
}
