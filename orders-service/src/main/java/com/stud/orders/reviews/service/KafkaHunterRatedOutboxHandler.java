package com.stud.orders.reviews.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stud.orders.common.messaging.KafkaTopicsProperties;
import com.stud.orders.common.outbox.OutboxEventHandler;
import com.stud.orders.reviews.api.HunterRatedEvent;
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
public class KafkaHunterRatedOutboxHandler implements OutboxEventHandler {

    private static final long SEND_TIMEOUT_SECONDS = 10;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final KafkaTopicsProperties topicsProperties;
    private final ObjectMapper objectMapper;

    @Override
    public String eventType() {
        return HunterRatedEvent.EVENT_TYPE;
    }

    @Override
    public void handle(String payload) {
        HunterRatedEvent event = readEvent(payload);
        try {
            kafkaTemplate.send(
                    topicsProperties.getHunterRated(),
                    event.hunterProfileId().toString(),
                    payload
            ).get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Kafka publish interrupted for " + eventType(), exception);
        } catch (ExecutionException | TimeoutException exception) {
            throw new IllegalStateException("Failed to publish Kafka event " + eventType(), exception);
        }
    }

    private HunterRatedEvent readEvent(String payload) {
        try {
            return objectMapper.readValue(payload, HunterRatedEvent.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to deserialize HunterRatedEvent", exception);
        }
    }
}
