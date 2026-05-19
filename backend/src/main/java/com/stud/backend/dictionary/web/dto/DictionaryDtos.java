package com.stud.backend.dictionary.web.dto;


import com.stud.backend.dictionary.domain.enums.FactionRelation;
import com.stud.backend.dictionary.domain.enums.FactionType;
import com.stud.backend.dictionary.domain.enums.PlanetStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public final class DictionaryDtos {

    private DictionaryDtos() {
    }

    public record FactionCreateRequest(
            @NotBlank @Size(max = 120) String name,
            String description,
            @NotNull FactionType type,
            @NotNull @Min(0) @Max(100) Integer influenceLevel,
            @NotNull FactionRelation relationToGuild
    ) {
    }

    public record FactionResponse(
            UUID id,
            String name,
            String description,
            FactionType type,
            Integer influenceLevel,
            FactionRelation relationToGuild
    ) {
    }

    public record SectorCreateRequest(
            UUID controllingFactionId,
            @NotBlank @Size(max = 120) String name,
            String description,
            @NotNull @Min(0) @Max(100) Integer stabilityLevel,
            @NotNull @Min(0) @Max(100) Integer dangerLevel
    ) {
    }

    public record SectorResponse(
            UUID id,
            UUID controllingFactionId,
            String name,
            String description,
            Integer stabilityLevel,
            Integer dangerLevel
    ) {
    }

    public record PlanetCreateRequest(
            @NotNull UUID sectorId,
            UUID controllingFactionId,
            @NotBlank @Size(max = 120) String name,
            String description,
            @NotNull @Min(0) @Max(100) Integer dangerLevel,
            @NotNull @Min(0) @Max(100) Integer developmentLevel,
            @Size(max = 120) String climate,
            Long population,
            @NotNull PlanetStatus status
    ) {
    }

    public record PlanetResponse(
            UUID id,
            UUID sectorId,
            UUID controllingFactionId,
            String name,
            String description,
            Integer dangerLevel,
            Integer developmentLevel,
            String climate,
            Long population,
            PlanetStatus status
    ) {
    }

    public record OrderCategoryCreateRequest(
            @NotBlank @Size(max = 120) String name,
            @NotBlank @Size(max = 140) String slug,
            String description,
            Boolean active
    ) {
    }

    public record OrderCategoryResponse(
            UUID id,
            String name,
            String slug,
            String description,
            Boolean active
    ) {
    }

    public record CurrencyCreateRequest(
            @NotBlank @Size(max = 16) String code,
            @NotBlank @Size(max = 80) String name,
            @Size(max = 16) String symbol,
            Boolean active
    ) {
    }

    public record CurrencyResponse(
            String code,
            String name,
            String symbol,
            Boolean active
    ) {
    }

    public record SkillCreateRequest(
            @NotBlank @Size(max = 120) String name,
            String description
    ) {
    }

    public record SkillResponse(
            UUID id,
            String name,
            String description
    ) {
    }
}