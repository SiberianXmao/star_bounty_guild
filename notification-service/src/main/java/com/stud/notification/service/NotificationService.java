package com.stud.notification.service;

import com.stud.notification.domain.Notification;
import com.stud.notification.domain.NotificationRecipientType;
import com.stud.notification.messaging.ApplicationAcceptedEvent;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    void createApplicationAcceptedNotification(ApplicationAcceptedEvent event, String payload);

    List<Notification> getNotifications(NotificationRecipientType recipientType, UUID recipientId);
}
