package com.stud.backend.applications.web.dto;


import com.stud.backend.applications.domain.enums.ApplicationStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public final class OrderApplicationDtos {

    private OrderApplicationDtos() {
    }

    public record ApplicationCreateRequest(
            @Size(max = 2000) String message,
            @DecimalMin("0.01") BigDecimal proposedReward
    ) {
    }

    public record ApplicationResponse(
            UUID id,

            UUID orderId,
            String orderTitle,
            String orderStatus,

            UUID hunterId,
            String hunterCallsign,

            String message,
            BigDecimal proposedReward,
            ApplicationStatus status,

            Instant createdAt,
            Instant updatedAt
    ) {
    }
}
