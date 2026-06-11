package com.stud.backend.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stud.backend.applications.api.ApplicationAcceptedEvent;
import com.stud.backend.common.outbox.OutboxEventHandler;
import com.stud.backend.orders.api.OrderAssignmentUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApplicationAcceptedOutboxHandler implements OutboxEventHandler {

    private final ObjectMapper objectMapper;
    private final OrderAssignmentUpdater orderAssignmentUpdater;

    @Override
    public String eventType() {
        return ApplicationAcceptedEvent.EVENT_TYPE;
    }

    @Override
    public void handle(String payload) {
        ApplicationAcceptedEvent event = readEvent(payload);
        orderAssignmentUpdater.assignHunter(event.orderId(), event.hunterProfileId());
    }

    private ApplicationAcceptedEvent readEvent(String payload) {
        try {
            return objectMapper.readValue(payload, ApplicationAcceptedEvent.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to deserialize ApplicationAcceptedEvent", ex);
        }
    }
}