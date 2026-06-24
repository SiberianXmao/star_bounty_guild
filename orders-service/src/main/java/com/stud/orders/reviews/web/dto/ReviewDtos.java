package com.stud.orders.reviews.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class ReviewDtos {

    private ReviewDtos() {
    }

    public record ReviewCreateRequest(
            @NotNull @Min(1) @Max(5) Integer rating,
            @Size(max = 2000) String comment
    ) {
    }

    public record ReviewResponse(
            UUID id,
            UUID orderId,
            String orderTitle,
            UUID clientProfileId,
            String clientName,
            UUID hunterProfileId,
            String hunterCallsign,
            Integer rating,
            String comment,
            Instant createdAt
    ) {
    }

    public record ReviewPageResponse(
            List<ReviewResponse> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last
    ) {
    }
}
