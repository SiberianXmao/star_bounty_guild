package com.stud.orders.orders.service.support;

import com.stud.orders.integrations.dictionary.CurrencyRef;
import com.stud.orders.integrations.dictionary.OrderCategoryRef;
import com.stud.orders.integrations.dictionary.PlanetRef;
import com.stud.orders.integrations.dictionary.SectorRef;
import com.stud.orders.integrations.profiles.ClientProfileRef;
import com.stud.orders.orders.domain.BountyOrder;
import com.stud.orders.orders.mapper.OrderDraftMapper;
import com.stud.orders.orders.web.dto.OrderDtos.OrderCreateRequest;
import com.stud.orders.orders.web.dto.OrderDtos.OrderUpdateDraftRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

// резолвит справочники

@Component
@RequiredArgsConstructor
public class OrderDraftEditor {

    private final OrderReferenceResolver references;
    private final OrderDraftMapper mapper;

    public BountyOrder createDraft(ClientProfileRef clientProfile, OrderCreateRequest request) {
        OrderCategoryRef category = references.activeCategory(request.categoryId());
        CurrencyRef currency = references.activeCurrency(request.rewardCurrencyCode());
        PlanetRef planet = request.planetId() == null ? null : references.planet(request.planetId());
        SectorRef sector = request.sectorId() == null ? null : references.sector(request.sectorId());

        return mapper.toDraft(clientProfile, request, category, currency, planet, sector);
    }

    public void updateDraft(BountyOrder order, OrderUpdateDraftRequest request) {
        OrderCategoryRef category = request.categoryId() == null
                ? null
                : references.activeCategory(request.categoryId());
        CurrencyRef currency = request.rewardCurrencyCode() == null
                ? null
                : references.activeCurrency(request.rewardCurrencyCode());
        PlanetRef planet = request.planetId() == null ? null : references.planet(request.planetId());
        SectorRef sector = request.sectorId() == null ? null : references.sector(request.sectorId());

        mapper.updateDraft(order, request, category, currency, planet, sector);
    }
}
