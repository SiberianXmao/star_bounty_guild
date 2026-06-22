package com.stud.notification.service;

import com.stud.notification.service.impl.NotificationServiceImpl;
import com.stud.notification.domain.NotificationRecipientType;
import com.stud.notification.domain.NotificationType;
import com.stud.notification.messaging.ApplicationAcceptedEvent;
import com.stud.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    @Test
    void createApplicationAcceptedNotification_shouldSaveNotification() {
        NotificationRepository repository = mock(NotificationRepository.class);
        NotificationService service = new NotificationServiceImpl(repository);

        ApplicationAcceptedEvent event = new ApplicationAcceptedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        String payload = "{}";

        when(repository.existsByEventId(event.applicationId())).thenReturn(false);

        service.createApplicationAcceptedNotification(event, payload);

        ArgumentCaptor<com.stud.notification.domain.Notification> captor =
                ArgumentCaptor.forClass(com.stud.notification.domain.Notification.class);
        verify(repository).save(captor.capture());

        com.stud.notification.domain.Notification notification = captor.getValue();

        assertThat(notification.getEventId()).isEqualTo(event.applicationId());
        assertThat(notification.getEventType()).isEqualTo(ApplicationAcceptedEvent.EVENT_TYPE);
        assertThat(notification.getType()).isEqualTo(NotificationType.APPLICATION_ACCEPTED);
        assertThat(notification.getRecipientType()).isEqualTo(NotificationRecipientType.HUNTER_PROFILE);
        assertThat(notification.getRecipientId()).isEqualTo(event.hunterProfileId());
        assertThat(notification.getRelatedOrderId()).isEqualTo(event.orderId());
        assertThat(notification.getPayload()).isEqualTo(payload);
    }

    @Test
    void createApplicationAcceptedNotification_shouldIgnoreAlreadyConsumedEvent() {
        NotificationRepository repository = mock(NotificationRepository.class);
        NotificationService service = new NotificationServiceImpl(repository);

        ApplicationAcceptedEvent event = new ApplicationAcceptedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        when(repository.existsByEventId(event.applicationId())).thenReturn(true);

        service.createApplicationAcceptedNotification(event, "{}");

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
