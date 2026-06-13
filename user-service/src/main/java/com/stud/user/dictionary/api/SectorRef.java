package com.stud.user.dictionary.api;

import java.util.UUID;

public record SectorRef(
        UUID id,
        UUID controllingFactionId,
        String name,
        Integer stabilityLevel,
        Integer dangerLevel
) {
}
