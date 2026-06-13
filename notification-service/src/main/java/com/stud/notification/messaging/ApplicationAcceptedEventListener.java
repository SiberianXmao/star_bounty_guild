package com.stud.notification.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stud.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationAcceptedEventListener {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @KafkaListener(topics = "${app.kafka.topics.application-accepted}")
    public void handle(String payload) {
        ApplicationAcceptedEvent event = readEvent(payload);

        notificationService.createApplicationAcceptedNotification(event, payload);

        log.info(
                "Consumed application accepted event applicationId={}, orderId={}, hunterProfileId={}",
                event.applicationId(),
                event.orderId(),
                event.hunterProfileId()
        );
    }

    private ApplicationAcceptedEvent readEvent(String payload) {
        try {
            return objectMapper.readValue(payload, ApplicationAcceptedEvent.class);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to deserialize ApplicationAcceptedEvent", ex);
        }
    }
}
