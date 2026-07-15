package com.stud.orders.orders.mapper;

import com.stud.orders.integrations.dictionary.CurrencyRef;
import com.stud.orders.integrations.dictionary.OrderCategoryRef;
import com.stud.orders.integrations.dictionary.PlanetRef;
import com.stud.orders.integrations.dictionary.SectorRef;
import com.stud.orders.integrations.profiles.ClientProfileRef;
import com.stud.orders.orders.domain.BountyOrder;
import com.stud.orders.orders.domain.enums.AcceptanceMode;
import com.stud.orders.orders.domain.enums.OrderStatus;
import com.stud.orders.orders.domain.enums.OrderVisibility;
import com.stud.orders.orders.web.dto.OrderDtos.OrderCreateRequest;
import com.stud.orders.orders.web.dto.OrderDtos.OrderUpdateDraftRequest;
import org.springframework.stereotype.Component;

@Component
public class OrderDraftMapper {

    public BountyOrder toDraft(
            ClientProfileRef clientProfile,
            OrderCreateRequest request,
            OrderCategoryRef category,
            CurrencyRef currency,
            PlanetRef planet,
            SectorRef sector
    ) {
        BountyOrder order = new BountyOrder();
        order.setClientId(clientProfile.id());
        order.setAssignedHunterId(null);
        order.setTitle(request.title().trim());
        order.setDescription(request.description().trim());
        order.setCategoryId(category.id());
        order.setRewardAmount(request.rewardAmount());
        order.setRewardCurrencyCode(currency.code());
        order.setPlanetId(planet == null ? null : planet.id());
        order.setSectorId(sector == null ? null : sector.id());
        order.setRiskLevel(request.riskLevel());
        order.setUrgencyLevel(request.urgencyLevel());
        order.setStatus(OrderStatus.DRAFT);
        order.setVisibility(request.visibility() == null ? OrderVisibility.PUBLIC : request.visibility());
        order.setAcceptanceMode(request.acceptanceMode() == null
                ? AcceptanceMode.APPLICATIONS
                : request.acceptanceMode());
        order.setRequirements(request.requirements());
        order.setDeadline(request.deadline());

        return order;
    }

    public void updateDraft(
            BountyOrder order,
            OrderUpdateDraftRequest request,
            OrderCategoryRef category,
            CurrencyRef currency,
            PlanetRef planet,
            SectorRef sector
    ) {
        if (request.title() != null) {
            order.setTitle(request.title().trim());
        }

        if (request.description() != null) {
            order.setDescription(request.description().trim());
        }

        if (category != null) {
            order.setCategoryId(category.id());
        }

        if (request.rewardAmount() != null) {
            order.setRewardAmount(request.rewardAmount());
        }

        if (currency != null) {
            order.setRewardCurrencyCode(currency.code());
        }

        if (planet != null) {
            order.setPlanetId(planet.id());
        }

        if (sector != null) {
            order.setSectorId(sector.id());
        }

        if (request.riskLevel() != null) {
            order.setRiskLevel(request.riskLevel());
        }

        if (request.urgencyLevel() != null) {
            order.setUrgencyLevel(request.urgencyLevel());
        }

        if (request.visibility() != null) {
            order.setVisibility(request.visibility());
        }

        if (request.acceptanceMode() != null) {
            order.setAcceptanceMode(request.acceptanceMode());
        }

        if (request.requirements() != null) {
            order.setRequirements(request.requirements());
        }

        if (request.deadline() != null) {
            order.setDeadline(request.deadline());
        }
    }
}
