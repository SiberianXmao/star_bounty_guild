package com.stud.orders.applications.api;

import java.util.UUID;

public record ApplicationAcceptedEvent(
        UUID applicationId,
        UUID orderId,
        UUID hunterProfileId
) {
    public static final String EVENT_TYPE = "applications.application-accepted.v1";
    public static final String AGGREGATE_TYPE = "ORDER_APPLICATION";
}
