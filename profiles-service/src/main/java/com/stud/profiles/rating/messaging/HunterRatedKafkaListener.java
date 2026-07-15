package com.stud.profiles.rating.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stud.profiles.rating.service.HunterRatingProjection;
import com.stud.profiles.rating.service.HunterRatingProjection.RatingEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HunterRatedKafkaListener {

    private final ObjectMapper objectMapper;
    private final HunterRatingProjection ratingProjection;

    @KafkaListener(topics = "${app.kafka.topics.hunter-rated}")
    public void onHunterRated(String payload) {
        RatingEvent event = readEvent(payload);
        ratingProjection.apply(event);
        log.info(
                "Consumed hunter rated event reviewId={}, orderId={}, hunterProfileId={}, rating={}",
                event.reviewId(),
                event.orderId(),
                event.hunterProfileId(),
                event.rating()
        );
    }

    private RatingEvent readEvent(String payload) {
        try {
            return objectMapper.readValue(payload, RatingEvent.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid hunter rating event payload", exception);
        }
    }
}
