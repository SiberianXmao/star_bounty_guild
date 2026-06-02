package com.stud.backend.orders.repository;


import com.stud.backend.orders.domain.BountyOrder;
import com.stud.backend.orders.domain.enums.AcceptanceMode;
import com.stud.backend.orders.domain.enums.OrderStatus;
import com.stud.backend.orders.domain.enums.OrderVisibility;
import com.stud.backend.orders.domain.enums.RiskLevel;
import com.stud.backend.orders.domain.enums.UrgencyLevel;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.UUID;

public final class BountyOrderSpecifications {

    private BountyOrderSpecifications() {
    }

    public static Specification<BountyOrder> publicBoard() {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("visibility"), OrderVisibility.PUBLIC),
                cb.equal(root.get("status"), OrderStatus.OPEN)
        );
    }

    public static Specification<BountyOrder> categoryId(UUID categoryId) {
        return categoryId == null
                ? null
                : (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<BountyOrder> planetId(UUID planetId) {
        return planetId == null
                ? null
                : (root, query, cb) -> cb.equal(root.get("planet").get("id"), planetId);
    }

    public static Specification<BountyOrder> sectorId(UUID sectorId) {
        return sectorId == null
                ? null
                : (root, query, cb) -> cb.equal(root.get("sector").get("id"), sectorId);
    }

    public static Specification<BountyOrder> riskLevel(RiskLevel riskLevel) {
        return riskLevel == null
                ? null
                : (root, query, cb) -> cb.equal(root.get("riskLevel"), riskLevel);
    }

    public static Specification<BountyOrder> urgencyLevel(UrgencyLevel urgencyLevel) {
        return urgencyLevel == null
                ? null
                : (root, query, cb) -> cb.equal(root.get("urgencyLevel"), urgencyLevel);
    }

    public static Specification<BountyOrder> acceptanceMode(AcceptanceMode acceptanceMode) {
        return acceptanceMode == null
                ? null
                : (root, query, cb) -> cb.equal(root.get("acceptanceMode"), acceptanceMode);
    }

    public static Specification<BountyOrder> rewardMin(BigDecimal rewardMin) {
        return rewardMin == null
                ? null
                : (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("rewardAmount"), rewardMin);
    }

    public static Specification<BountyOrder> rewardMax(BigDecimal rewardMax) {
        return rewardMax == null
                ? null
                : (root, query, cb) -> cb.lessThanOrEqualTo(root.get("rewardAmount"), rewardMax);
    }

    public static Specification<BountyOrder> search(String q) {
        if (q == null || q.isBlank()) {
            return null;
        }

        String pattern = "%" + q.trim().toLowerCase() + "%";

        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), pattern),
                cb.like(cb.lower(root.get("description")), pattern)
        );
    }
}