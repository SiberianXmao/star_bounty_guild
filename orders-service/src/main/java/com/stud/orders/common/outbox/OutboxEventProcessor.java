package com.stud.orders.common.outbox;

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
            List<OutboxEventHandler> matchingHandlers = findHandlers(event.getEventType());

            for (OutboxEventHandler handler : matchingHandlers) {
                handler.handle(event.getPayload());
            }

            event.markProcessed();

            log.info(
                    "Processed outbox event id={}, type={}, aggregateType={}, aggregateId={}, handlers={}",
                    event.getId(),
                    event.getEventType(),
                    event.getAggregateType(),
                    event.getAggregateId(),
                    matchingHandlers.size()
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

    private List<OutboxEventHandler> findHandlers(String eventType) {
        List<OutboxEventHandler> matchingHandlers = handlers.stream()
                .filter(handler -> handler.eventType().equals(eventType))
                .toList();

        if (matchingHandlers.isEmpty()) {
            throw new IllegalStateException("No outbox handler for event type: " + eventType);
        }

        return matchingHandlers;
    }

    private String toErrorMessage(Exception ex) {
        String message = ex.getMessage();

        if (message == null || message.isBlank()) {
            return ex.getClass().getSimpleName();
        }

        return ex.getClass().getSimpleName() + ": " + message;
    }
}
