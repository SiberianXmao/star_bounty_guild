package com.stud.backend.common.outbox;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OutboxEventTest {

    @Test
    void markFailed_shouldSaveShortErrorAndKeepPendingBeforeMaxAttempts() {
        OutboxEvent event = new OutboxEvent();

        event.markFailed("Database timeout", 3, 2000);

        assertThat(event.getAttempts()).isEqualTo(1);
        assertThat(event.getLastError()).isEqualTo("Database timeout");
        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PENDING);
    }

    @Test
    void markFailed_shouldUseFallbackForBlankError() {
        OutboxEvent event = new OutboxEvent();

        event.markFailed("   ", 3, 2000);

        assertThat(event.getLastError()).isEqualTo("Unknown outbox processing error");
    }

    @Test
    void markFailed_shouldTrimLongError() {
        OutboxEvent event = new OutboxEvent();
        String longError = "x".repeat(20);

        event.markFailed(longError, 3, 10);

        assertThat(event.getLastError()).hasSize(10);
        assertThat(event.getLastError()).isEqualTo("x".repeat(10));
    }

    @Test
    void markFailed_shouldMarkFailedWhenMaxAttemptsReached() {
        OutboxEvent event = new OutboxEvent();

        event.markFailed("First fail", 3, 2000);
        event.markFailed("Second fail", 3, 2000);
        event.markFailed("Third fail", 3, 2000);

        assertThat(event.getAttempts()).isEqualTo(3);
        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.FAILED);
    }

    @Test
    void markProcessed_shouldMarkProcessedAndClearError() {
        OutboxEvent event = new OutboxEvent();
        event.markFailed("Temporary fail", 3, 2000);

        event.markProcessed();

        assertThat(event.getStatus()).isEqualTo(OutboxEventStatus.PROCESSED);
        assertThat(event.getLastError()).isNull();
        assertThat(event.getProcessedAt()).isNotNull();
    }
}