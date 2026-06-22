package com.stud.user.profiles.api;

import java.math.BigDecimal;
import java.util.UUID;

public record ClientProfileRef(
        UUID id,
        UUID userId,
        String name,
        BigDecimal averageRating,
        Integer reliabilityScore
) {
}
