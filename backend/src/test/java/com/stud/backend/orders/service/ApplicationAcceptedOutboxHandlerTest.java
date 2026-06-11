package com.stud.backend.orders.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stud.backend.applications.api.ApplicationAcceptedEvent;
import com.stud.backend.orders.api.OrderAssignmentUpdater;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class ApplicationAcceptedOutboxHandlerTest {

    @Test
    void eventType_shouldReturnApplicationAcceptedEventType() {
        OrderAssignmentUpdater orderAssignmentUpdater = mock(OrderAssignmentUpdater.class);
        ApplicationAcceptedOutboxHandler handler =
                new ApplicationAcceptedOutboxHandler(new ObjectMapper(), orderAssignmentUpdater);

        assertThat(handler.eventType()).isEqualTo(ApplicationAcceptedEvent.EVENT_TYPE);
    }

    @Test
    void handle_shouldAssignHunterFromPayload() throws Exception {
        OrderAssignmentUpdater orderAssignmentUpdater = mock(OrderAssignmentUpdater.class);
        ObjectMapper objectMapper = new ObjectMapper();

        ApplicationAcceptedOutboxHandler handler =
                new ApplicationAcceptedOutboxHandler(objectMapper, orderAssignmentUpdater);

        UUID applicationId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID hunterProfileId = UUID.randomUUID();

        ApplicationAcceptedEvent event = new ApplicationAcceptedEvent(
                applicationId,
                orderId,
                hunterProfileId
        );

        handler.handle(objectMapper.writeValueAsString(event));

        verify(orderAssignmentUpdater).assignHunter(orderId, hunterProfileId);
    }

    @Test
    void handle_shouldThrowExceptionForInvalidPayload() {
        OrderAssignmentUpdater orderAssignmentUpdater = mock(OrderAssignmentUpdater.class);
        ApplicationAcceptedOutboxHandler handler =
                new ApplicationAcceptedOutboxHandler(new ObjectMapper(), orderAssignmentUpdater);

        assertThatThrownBy(() -> handler.handle("{ broken-json"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Failed to deserialize ApplicationAcceptedEvent");

        verifyNoInteractions(orderAssignmentUpdater);
    }
}