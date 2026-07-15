package com.stud.orders.orders.mapper;

import com.stud.orders.integrations.dictionary.CurrencyRef;
import com.stud.orders.integrations.dictionary.OrderCategoryRef;
import com.stud.orders.integrations.dictionary.PlanetRef;
import com.stud.orders.integrations.dictionary.SectorRef;
import com.stud.orders.integrations.profiles.ClientProfileRef;
import com.stud.orders.integrations.profiles.HunterProfileRef;
import com.stud.orders.orders.domain.BountyOrder;
import com.stud.orders.orders.web.dto.OrderDtos.OrderResponse;
import com.stud.orders.orders.web.dto.OrderDtos.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderResponseMapper {

    public OrderResponse toResponse(
            BountyOrder order,
            ClientProfileRef client,
            HunterProfileRef hunter,
            OrderCategoryRef category,
            CurrencyRef currency,
            PlanetRef planet,
            SectorRef sector
    ) {
        return new OrderResponse(
                order.getId(),
                client.id(),
                client.name(),
                client.averageRating(),
                client.reliabilityScore(),
                hunter == null ? null : hunter.id(),
                hunter == null ? null : hunter.callsign(),
                order.getTitle(),
                order.getDescription(),
                category.id(),
                category.name(),
                order.getRewardAmount(),
                currency.code(),
                currency.name(),
                currency.symbol(),
                planet == null ? null : planet.id(),
                planet == null ? null : planet.name(),
                sector == null ? null : sector.id(),
                sector == null ? null : sector.name(),
                order.getRiskLevel(),
                order.getUrgencyLevel(),
                order.getStatus(),
                order.getVisibility(),
                order.getAcceptanceMode(),
                order.getRequirements(),
                order.getDeadline(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getPublishedAt(),
                order.getCompletedAt()
        );
    }

    public <T> PageResponse<T> toPageResponse(Page<?> page, List<T> content) {
        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}
