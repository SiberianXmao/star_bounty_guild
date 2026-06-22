package com.stud.notification.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stud.notification.service.NotificationService;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class ApplicationAcceptedEventListenerTest {

    @Test
    void handle_shouldCreateNotificationFromPayload() throws Exception {
        NotificationService notificationService = mock(NotificationService.class);
        ObjectMapper objectMapper = new ObjectMapper();
        ApplicationAcceptedEventListener listener =
                new ApplicationAcceptedEventListener(objectMapper, notificationService);

        ApplicationAcceptedEvent event = new ApplicationAcceptedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );
        String payload = objectMapper.writeValueAsString(event);

        listener.handle(payload);

        verify(notificationService).createApplicationAcceptedNotification(event, payload);
    }

    @Test
    void handle_shouldThrowExceptionForInvalidPayload() {
        NotificationService notificationService = mock(NotificationService.class);
        ApplicationAcceptedEventListener listener =
                new ApplicationAcceptedEventListener(new ObjectMapper(), notificationService);

        assertThatThrownBy(() -> listener.handle("{ broken-json"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Failed to deserialize ApplicationAcceptedEvent");

        verifyNoInteractions(notificationService);
    }
}
