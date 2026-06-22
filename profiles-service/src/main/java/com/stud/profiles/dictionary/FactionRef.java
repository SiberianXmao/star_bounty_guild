package com.stud.profiles.dictionary;

import java.util.UUID;

public record FactionRef(
        UUID id,
        String name,
        String type,
        String relationToGuild
) {
}
