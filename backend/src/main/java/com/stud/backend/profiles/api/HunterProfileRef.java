package com.stud.backend.profiles.api;

import java.math.BigDecimal;
import java.util.UUID;

public record HunterProfileRef(
        UUID id,
        UUID userId,
        String callsign,
        BigDecimal averageRating,
        Integer reliabilityScore
) {
}
