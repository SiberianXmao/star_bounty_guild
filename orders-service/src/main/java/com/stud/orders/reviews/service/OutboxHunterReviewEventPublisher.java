package com.stud.orders.reviews.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stud.orders.common.outbox.OutboxEvent;
import com.stud.orders.common.outbox.OutboxEventRepository;
import com.stud.orders.reviews.api.HunterRatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxHunterReviewEventPublisher implements HunterReviewEventPublisher {

    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(HunterRatedEvent event) {
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setAggregateType(HunterRatedEvent.AGGREGATE_TYPE);
        outboxEvent.setAggregateId(event.reviewId());
        outboxEvent.setEventType(HunterRatedEvent.EVENT_TYPE);
        outboxEvent.setPayload(toPayload(event));
        outboxEventRepository.save(outboxEvent);
    }

    private String toPayload(HunterRatedEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize hunter rating event", exception);
        }
    }
}
