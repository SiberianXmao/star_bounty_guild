package com.stud.orders.orders.service.support;

import com.stud.orders.common.exception.ResourceNotFoundException;
import com.stud.orders.integrations.dictionary.CurrencyRef;
import com.stud.orders.integrations.dictionary.OrderCategoryRef;
import com.stud.orders.integrations.dictionary.PlanetRef;
import com.stud.orders.integrations.dictionary.SectorRef;
import com.stud.orders.integrations.profiles.ClientProfileRef;
import com.stud.orders.integrations.profiles.HunterProfileRef;
import com.stud.orders.orders.domain.BountyOrder;
import com.stud.orders.orders.mapper.OrderResponseMapper;
import com.stud.orders.orders.web.dto.OrderDtos.OrderResponse;
import com.stud.orders.orders.web.dto.OrderDtos.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderResponseAssembler {

    private final OrderReferenceResolver references;
    private final OrderResponseMapper mapper;

    public OrderResponse toResponse(BountyOrder order) {
        ClientProfileRef client = references.clientProfile(order.getClientId());
        HunterProfileRef hunter = order.getAssignedHunterId() == null
                ? null
                : references.hunterProfile(order.getAssignedHunterId());
        OrderCategoryRef category = references.category(order.getCategoryId());
        CurrencyRef currency = references.currency(order.getRewardCurrencyCode());
        PlanetRef planet = order.getPlanetId() == null ? null : references.planet(order.getPlanetId());
        SectorRef sector = order.getSectorId() == null ? null : references.sector(order.getSectorId());

        return mapper.toResponse(order, client, hunter, category, currency, planet, sector);
    }

    public PageResponse<OrderResponse> toPageResponse(Page<BountyOrder> page) {
        return mapper.toPageResponse(page, toResponses(page.getContent()));
    }

    public List<OrderResponse> toResponses(List<BountyOrder> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }

        Map<UUID, ClientProfileRef> clientsById = references.clientProfilesByIds(
                orders.stream()
                        .map(BountyOrder::getClientId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );
        Map<UUID, HunterProfileRef> huntersById = references.hunterProfilesByIds(
                orders.stream()
                        .map(BountyOrder::getAssignedHunterId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );
        Map<UUID, OrderCategoryRef> categoriesById = references.categoriesByIds(
                orders.stream()
                        .map(BountyOrder::getCategoryId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );
        Map<String, CurrencyRef> currenciesByCode = references.currenciesByCodes(
                orders.stream()
                        .map(BountyOrder::getRewardCurrencyCode)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );
        Map<UUID, PlanetRef> planetsById = references.planetsByIds(
                orders.stream()
                        .map(BountyOrder::getPlanetId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );
        Map<UUID, SectorRef> sectorsById = references.sectorsByIds(
                orders.stream()
                        .map(BountyOrder::getSectorId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );

        return orders.stream()
                .map(order -> toResponse(
                        order,
                        clientsById,
                        huntersById,
                        categoriesById,
                        currenciesByCode,
                        planetsById,
                        sectorsById
                ))
                .toList();
    }

    private OrderResponse toResponse(
            BountyOrder order,
            Map<UUID, ClientProfileRef> clientsById,
            Map<UUID, HunterProfileRef> huntersById,
            Map<UUID, OrderCategoryRef> categoriesById,
            Map<String, CurrencyRef> currenciesByCode,
            Map<UUID, PlanetRef> planetsById,
            Map<UUID, SectorRef> sectorsById
    ) {
        ClientProfileRef client = clientsById.get(order.getClientId());
        HunterProfileRef hunter = order.getAssignedHunterId() == null
                ? null
                : huntersById.get(order.getAssignedHunterId());
        OrderCategoryRef category = categoriesById.get(order.getCategoryId());
        CurrencyRef currency = currenciesByCode.get(order.getRewardCurrencyCode());
        PlanetRef planet = order.getPlanetId() == null ? null : planetsById.get(order.getPlanetId());
        SectorRef sector = order.getSectorId() == null ? null : sectorsById.get(order.getSectorId());

        if (client == null) {
            throw new ResourceNotFoundException("Client profile not found: " + order.getClientId());
        }

        if (category == null) {
            throw new ResourceNotFoundException("Order category not found: " + order.getCategoryId());
        }

        if (currency == null) {
            throw new ResourceNotFoundException("Currency not found: " + order.getRewardCurrencyCode());
        }

        return mapper.toResponse(order, client, hunter, category, currency, planet, sector);
    }
}
