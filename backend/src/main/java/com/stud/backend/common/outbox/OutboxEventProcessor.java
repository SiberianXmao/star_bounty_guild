package com.stud.backend.common.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventProcessor {


    private final OutboxEventRepository outboxEventRepository;
    private final List<OutboxEventHandler> handlers;
    private final OutboxProperties outboxProperties;

    @Scheduled(fixedDelayString = "${app.outbox.poll-delay-ms:5000}")
    @Transactional
    public void processPendingEvents() {
        List<OutboxEvent> events = outboxEventRepository
                .findPendingBatchForUpdate(outboxProperties.getBatchSize());

        for (OutboxEvent event : events) {
            processEvent(event);
        }
    }

    private void processEvent(OutboxEvent event) {
        try {
            findHandler(event.getEventType()).handle(event.getPayload());
            event.markProcessed();

            log.info(
                    "Processed outbox event id={}, type={}, aggregateType={}, aggregateId={}",
                    event.getId(),
                    event.getEventType(),
                    event.getAggregateType(),
                    event.getAggregateId()
            );
        } catch (Exception ex) {
            event.markFailed(
                    toErrorMessage(ex),
                    outboxProperties.getMaxAttempts(),
                    outboxProperties.getMaxErrorLength()
            );

            if (event.getStatus() == OutboxEventStatus.FAILED) {
                log.error(
                        "Outbox event permanently failed id={}, type={}, attempts={}",
                        event.getId(),
                        event.getEventType(),
                        event.getAttempts(),
                        ex
                );
            } else {
                log.warn(
                        "Outbox event processing failed id={}, type={}, attempts={}",
                        event.getId(),
                        event.getEventType(),
                        event.getAttempts(),
                        ex
                );
            }
        }
    }

    private OutboxEventHandler findHandler(String eventType) {
        return handlers.stream()
                .filter(handler -> handler.eventType().equals(eventType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No outbox handler for event type: " + eventType));
    }

    private String toErrorMessage(Exception ex) {
        String message = ex.getMessage();

        if (message == null || message.isBlank()) {
            return ex.getClass().getSimpleName();
        }

        return ex.getClass().getSimpleName() + ": " + message;
    }
}