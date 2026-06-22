package com.stud.user.dictionary.api;

import java.util.UUID;

public record FactionRef(
        UUID id,
        String name,
        String type,
        String relationToGuild
) {
}
