package com.stud.notification.service;

import com.stud.notification.domain.Notification;
import com.stud.notification.domain.NotificationRecipientType;
import com.stud.notification.domain.NotificationType;
import com.stud.notification.messaging.ApplicationAcceptedEvent;
import com.stud.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional
    public void createApplicationAcceptedNotification(ApplicationAcceptedEvent event, String payload) {
        if (notificationRepository.existsByEventId(event.applicationId())) {
            return;
        }

        Notification notification = new Notification();
        notification.setEventId(event.applicationId());
        notification.setEventType(ApplicationAcceptedEvent.EVENT_TYPE);
        notification.setType(NotificationType.APPLICATION_ACCEPTED);
        notification.setRecipientType(NotificationRecipientType.HUNTER_PROFILE);
        notification.setRecipientId(event.hunterProfileId());
        notification.setRelatedOrderId(event.orderId());
        notification.setTitle("Application accepted");
        notification.setMessage("Your application was accepted and you were assigned to the order.");
        notification.setPayload(payload);

        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotifications(NotificationRecipientType recipientType, UUID recipientId) {
        return notificationRepository.findAllByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(
                recipientType,
                recipientId
        );
    }
}
