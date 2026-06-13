package com.stud.notification.repository;

import com.stud.notification.domain.Notification;
import com.stud.notification.domain.NotificationRecipientType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    boolean existsByEventId(UUID eventId);

    List<Notification> findAllByRecipientTypeAndRecipientIdOrderByCreatedAtDesc(
            NotificationRecipientType recipientType,
            UUID recipientId
    );
}
