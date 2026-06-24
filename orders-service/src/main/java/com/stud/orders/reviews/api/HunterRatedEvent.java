package com.stud.orders.reviews.api;

import java.time.Instant;
import java.util.UUID;

public record HunterRatedEvent(
        UUID reviewId,
        UUID orderId,
        UUID hunterProfileId,
        Integer rating,
        Instant createdAt
) {
    public static final String EVENT_TYPE = "reviews.hunter-rated.v1";
    public static final String AGGREGATE_TYPE = "HUNTER_REVIEW";
}
