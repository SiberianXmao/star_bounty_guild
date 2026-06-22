package com.stud.orders.applications.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stud.orders.applications.api.ApplicationAcceptedEvent;
import com.stud.orders.common.messaging.KafkaTopicsProperties;
import com.stud.orders.common.outbox.OutboxEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Order(200)
@RequiredArgsConstructor
public class KafkaApplicationAcceptedOutboxHandler implements OutboxEventHandler {

    private static final long SEND_TIMEOUT_SECONDS = 10;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTopicsProperties topicsProperties;
    private final ObjectMapper objectMapper;

    @Override
    public String eventType() {
        return ApplicationAcceptedEvent.EVENT_TYPE;
    }

    @Override
    public void handle(String payload) {
        ApplicationAcceptedEvent event = readEvent(payload);

        try {
            kafkaTemplate.send(
                    topicsProperties.getApplicationAccepted(),
                    event.applicationId().toString(),
                    payload
            ).get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Kafka publish interrupted for " + eventType(), ex);
        } catch (ExecutionException | TimeoutException ex) {
            throw new IllegalStateException("Failed to publish Kafka event " + eventType(), ex);
        }
    }

    private ApplicationAcceptedEvent readEvent(String payload) {
        try {
            return objectMapper.readValue(payload, ApplicationAcceptedEvent.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to deserialize ApplicationAcceptedEvent", ex);
        }
    }
}
