package com.stud.backend.dictionary.api;

import java.util.UUID;

public record FactionRef(
        UUID id,
        String name,
        String type,
        String relationToGuild
) {
}
