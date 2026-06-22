package com.stud.orders.orders.web.dto;


import com.stud.orders.orders.domain.enums.AcceptanceMode;
import com.stud.orders.orders.domain.enums.OrderStatus;
import com.stud.orders.orders.domain.enums.OrderVisibility;
import com.stud.orders.orders.domain.enums.RiskLevel;
import com.stud.orders.orders.domain.enums.UrgencyLevel;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public final class OrderDtos {

    private OrderDtos() {
    }

    public record OrderCreateRequest(
            @NotBlank @Size(max = 200) String title,
            @NotBlank String description,
            @NotNull UUID categoryId,
            @NotNull @DecimalMin("0.01") BigDecimal rewardAmount,
            @NotBlank @Size(max = 16) String rewardCurrencyCode,
            UUID planetId,
            UUID sectorId,
            @NotNull RiskLevel riskLevel,
            @NotNull UrgencyLevel urgencyLevel,
            OrderVisibility visibility,
            AcceptanceMode acceptanceMode,
            String requirements,
            Instant deadline
    ) {
    }

    public record OrderUpdateDraftRequest(
            @Size(max = 200) String title,
            String description,
            UUID categoryId,
            @DecimalMin("0.01") BigDecimal rewardAmount,
            @Size(max = 16) String rewardCurrencyCode,
            UUID planetId,
            UUID sectorId,
            RiskLevel riskLevel,
            UrgencyLevel urgencyLevel,
            OrderVisibility visibility,
            AcceptanceMode acceptanceMode,
            String requirements,
            Instant deadline
    ) {
    }

    public record OrderResponse(
            UUID id,

            UUID clientId,
            String clientName,
            BigDecimal clientAverageRating,
            Integer clientReliabilityScore,

            UUID assignedHunterId,
            String assignedHunterCallsign,

            String title,
            String description,

            UUID categoryId,
            String categoryName,

            BigDecimal rewardAmount,
            String rewardCurrencyCode,
            String rewardCurrencyName,
            String rewardCurrencySymbol,

            UUID planetId,
            String planetName,

            UUID sectorId,
            String sectorName,

            RiskLevel riskLevel,
            UrgencyLevel urgencyLevel,
            OrderStatus status,
            OrderVisibility visibility,
            AcceptanceMode acceptanceMode,

            String requirements,
            Instant deadline,
            Instant createdAt,
            Instant updatedAt,
            Instant publishedAt,
            Instant completedAt
    ) {
    }

    public record PageResponse<T>(
            List<T> content,
            int page,
            int size,
            long totalElements,
            int totalPages,
            boolean first,
            boolean last
    ) {
    }
}
