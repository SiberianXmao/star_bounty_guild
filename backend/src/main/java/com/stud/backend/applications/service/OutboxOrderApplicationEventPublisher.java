package com.stud.backend.applications.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stud.backend.applications.api.ApplicationAcceptedEvent;
import com.stud.backend.applications.api.OrderApplicationEventPublisher;
import com.stud.backend.common.outbox.OutboxEvent;
import com.stud.backend.common.outbox.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxOrderApplicationEventPublisher implements OrderApplicationEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(ApplicationAcceptedEvent event) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateType(ApplicationAcceptedEvent.AGGREGATE_TYPE);
        outboxEvent.setAggregateId(event.applicationId());
        outboxEvent.setEventType(ApplicationAcceptedEvent.EVENT_TYPE);
        outboxEvent.setPayload(toPayload(event));

        outboxEventRepository.save(outboxEvent);
    }

    private String toPayload(ApplicationAcceptedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize outbox event", ex);
        }
    }
}