package com.stud.orders.orders.service.support;

import com.stud.orders.orders.domain.BountyOrder;
import com.stud.orders.orders.domain.enums.AcceptanceMode;
import com.stud.orders.orders.domain.enums.RiskLevel;
import com.stud.orders.orders.domain.enums.UrgencyLevel;
import com.stud.orders.orders.repository.BountyOrderSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

// Фильтры публичных заказов

@Component
public class OrderSearchSpecBuilder {

    public Specification<BountyOrder> publicBoard(
            UUID categoryId,
            UUID planetId,
            UUID sectorId,
            RiskLevel riskLevel,
            UrgencyLevel urgencyLevel,
            AcceptanceMode acceptanceMode,
            BigDecimal rewardMin,
            BigDecimal rewardMax,
            String q
    ) {
        return Specification.<BountyOrder>unrestricted()
                .and(BountyOrderSpecifications.publicBoard())
                .and(BountyOrderSpecifications.categoryId(categoryId))
                .and(BountyOrderSpecifications.planetId(planetId))
                .and(BountyOrderSpecifications.sectorId(sectorId))
                .and(BountyOrderSpecifications.riskLevel(riskLevel))
                .and(BountyOrderSpecifications.urgencyLevel(urgencyLevel))
                .and(BountyOrderSpecifications.acceptanceMode(acceptanceMode))
                .and(BountyOrderSpecifications.rewardMin(rewardMin))
                .and(BountyOrderSpecifications.rewardMax(rewardMax))
                .and(BountyOrderSpecifications.search(q));
    }
}
