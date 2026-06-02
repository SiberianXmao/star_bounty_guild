package com.stud.backend.profiles.web.dto;


import com.stud.backend.profiles.domain.enums.AvailabilityStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// переписать дто отдельно
public final class ProfileDtos {

    private ProfileDtos() {
    }

    public record MyProfilesResponse(
            ClientProfileResponse clientProfile,
            HunterProfileResponse hunterProfile
    ) {
    }

    public record ClientProfileCreateRequest(
            @NotBlank @Size(max = 120) String name,
            String description,
            UUID factionId,
            UUID planetId
    ) {
    }

    public record ClientProfileResponse(
            UUID id,
            UUID userId,
            String name,
            String description,
            UUID factionId,
            String factionName,
            UUID planetId,
            String planetName,
            Integer reliabilityScore,
            BigDecimal averageRating,
            Integer completedOrdersCount,
            Integer cancelledOrdersCount
    ) {
    }

    public record HunterProfileCreateRequest(
            @NotBlank @Size(max = 120) String callsign,
            String bio,
            UUID factionId,
            UUID homePlanetId,
            AvailabilityStatus availabilityStatus,
            @DecimalMin("0.00") BigDecimal minReward
    ) {
    }

    public record HunterProfileResponse(
            UUID id,
            UUID userId,
            String callsign,
            String bio,
            UUID factionId,
            String factionName,
            UUID homePlanetId,
            String homePlanetName,
            AvailabilityStatus availabilityStatus,
            BigDecimal minReward,
            Integer reliabilityScore,
            BigDecimal averageRating,
            Integer completedOrdersCount,
            Integer failedOrdersCount,
            List<HunterSkillResponse> skills
    ) {
    }

    public record HunterSkillRequest(
            @NotNull UUID skillId,
            @NotNull @Min(1) @Max(100) Integer level
    ) {
    }

    public record HunterSkillResponse(
            UUID skillId,
            String skillName,
            Integer level
    ) {
    }
}