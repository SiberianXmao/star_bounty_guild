package com.stud.backend.common.outbox;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class OutboxEventProcessorTest {

    @Test
    void processPendingEvents_shouldMarkEventProcessedWhenHandlerSucceeds() {
        OutboxEventRepository repository = mock(OutboxEventRepository.class);
        OutboxEventHandler handler = mock(OutboxEventHandler.class);
        OutboxProperties properties = properties();

        OutboxEvent event = event("test.event.v1", "{}");

        when(repository.findPendingBatchForUpdate(100)).thenReturn(List.of(event));
        when(handler.eventType()).thenReturn("test.event.v1");

        new OutboxEventProcessor(repository, List.of(handler), properties).processPendingEvents();

        verify(handler).handle("{}");
        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PROCESSED);
        assertThat(event.getProcessedAt()).isNotNull();
    }

    @Test
    void processPendingEvents_shouldKeepPendingBeforeMaxAttempts() {
        OutboxEventRepository repository = mock(OutboxEventRepository.class);
        OutboxEventHandler handler = mock(OutboxEventHandler.class);
        OutboxProperties properties = properties();

        OutboxEvent event = event("test.event.v1", "{}");

        when(repository.findPendingBatchForUpdate(100)).thenReturn(List.of(event));
        when(handler.eventType()).thenReturn("test.event.v1");
        doThrow(new IllegalStateException("Temporary failure")).when(handler).handle("{}");

        new OutboxEventProcessor(repository, List.of(handler), properties).processPendingEvents();

        assertThat(event.getAttempts()).isEqualTo(1);
        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PENDING);
        assertThat(event.getLastError()).contains("IllegalStateException: Temporary failure");
    }

    @Test
    void processPendingEvents_shouldMarkFailedWhenMaxAttemptsReached() {
        OutboxEventRepository repository = mock(OutboxEventRepository.class);
        OutboxEventHandler handler = mock(OutboxEventHandler.class);
        OutboxProperties properties = properties();
        properties.setMaxAttempts(1);

        OutboxEvent event = event("test.event.v1", "{}");

        when(repository.findPendingBatchForUpdate(100)).thenReturn(List.of(event));
        when(handler.eventType()).thenReturn("test.event.v1");
        doThrow(new IllegalStateException("Permanent failure")).when(handler).handle("{}");

        new OutboxEventProcessor(repository, List.of(handler), properties).processPendingEvents();

        assertThat(event.getAttempts()).isEqualTo(1);
        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.FAILED);
    }

    private OutboxProperties properties() {
        OutboxProperties properties = new OutboxProperties();
        properties.setBatchSize(100);
        properties.setMaxAttempts(3);
        properties.setMaxErrorLength(2000);
        return properties;
    }

    private OutboxEvent event(String eventType, String payload) {
        OutboxEvent event = new OutboxEvent();
        event.setAggregateType("TEST");
        event.setEventType(eventType);
        event.setPayload(payload);
        return event;
    }
}