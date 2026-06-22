package com.stud.notification.messaging;

import java.util.UUID;

public record ApplicationAcceptedEvent(
        UUID applicationId,
        UUID orderId,
        UUID hunterProfileId
) {
    public static final String EVENT_TYPE = "applications.application-accepted.v1";
}
