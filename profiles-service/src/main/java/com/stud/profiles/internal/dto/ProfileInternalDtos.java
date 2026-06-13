package com.stud.profiles.internal.dto;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

public final class ProfileInternalDtos {

    private ProfileInternalDtos() {
    }

    public record UuidLookupRequest(Collection<UUID> ids) {
    }

    public record CompletedOrderProgressRequest(
            UUID clientProfileId,
            UUID hunterProfileId
    ) {
    }

    public record ClientProfileRef(
            UUID id,
            UUID userId,
            String name,
            BigDecimal averageRating,
            Integer reliabilityScore
    ) {
    }

    public record HunterProfileRef(
            UUID id,
            UUID userId,
            String callsign,
            BigDecimal averageRating,
            Integer reliabilityScore
    ) {
    }
}
