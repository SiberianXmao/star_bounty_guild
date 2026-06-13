package com.stud.notification.web;

import com.stud.notification.domain.Notification;
import com.stud.notification.domain.NotificationRecipientType;
import com.stud.notification.service.NotificationService;
import com.stud.notification.web.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> getNotifications(
            @RequestParam NotificationRecipientType recipientType,
            @RequestParam UUID recipientId
    ) {
        return notificationService.getNotifications(recipientType, recipientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getEventId(),
                notification.getEventType(),
                notification.getType().name(),
                notification.getRecipientType().name(),
                notification.getRecipientId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getRelatedOrderId(),
                notification.getCreatedAt()
        );
    }
}
