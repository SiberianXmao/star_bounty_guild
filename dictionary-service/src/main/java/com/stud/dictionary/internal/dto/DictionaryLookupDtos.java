package com.stud.dictionary.internal.dto;

import java.util.Collection;
import java.util.UUID;

public final class DictionaryLookupDtos {

    private DictionaryLookupDtos() {
    }

    public record UuidLookupRequest(Collection<UUID> ids) {
    }

    public record CurrencyLookupRequest(Collection<String> codes) {
    }

    public record CurrencyRef(
            String code,
            String name,
            String symbol,
            Boolean active
    ) {
    }

    public record FactionRef(
            UUID id,
            String name,
            String type,
            String relationToGuild
    ) {
    }

    public record OrderCategoryRef(
            UUID id,
            String name,
            String slug,
            Boolean active
    ) {
    }

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

    public record SectorRef(
            UUID id,
            UUID controllingFactionId,
            String name,
            Integer stabilityLevel,
            Integer dangerLevel
    ) {
    }

    public record SkillRef(
            UUID id,
            String name
    ) {
    }
}
