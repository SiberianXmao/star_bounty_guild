package com.stud.orders.integrations.profiles;

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
