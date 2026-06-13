package com.stud.user.dictionary.api;

import java.util.UUID;

public record PlanetRef(
        UUID id,
        UUID sectorId,
        UUID controllingFactionId,
        String name,
        Integer dangerLevel,
        Integer developmentLevel,
        String status
) {
}
