package com.stud.orders.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stud.orders.applications.api.ApplicationAcceptedEvent;
import com.stud.orders.common.outbox.OutboxEventHandler;
import com.stud.orders.orders.api.OrderAssignmentUpdater;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)
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
