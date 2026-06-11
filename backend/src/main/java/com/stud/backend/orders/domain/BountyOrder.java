package com.stud.backend.orders.domain;

import com.stud.backend.common.persistence.BaseUuidEntity;
import com.stud.backend.orders.domain.enums.AcceptanceMode;
import com.stud.backend.orders.domain.enums.OrderStatus;
import com.stud.backend.orders.domain.enums.OrderVisibility;
import com.stud.backend.orders.domain.enums.RiskLevel;
import com.stud.backend.orders.domain.enums.UrgencyLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders", schema = "bounty")
public class BountyOrder extends BaseUuidEntity {

    @Column(name = "client_id", nullable = false)
    private UUID clientId;

    @Column(name = "assigned_hunter_id")
    private UUID assignedHunterId;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    //-----
    @Column(name = "category_id", nullable = false)
    private UUID categoryId;

    @Column(name = "reward_amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal rewardAmount;

    //------
    @Column(name = "reward_currency_code", nullable = false, length = 16)
    private String rewardCurrencyCode;

    // -----
    @Column(name = "planet_id")
    private UUID planetId;

    // ----
    @Column(name = "sector_id")
    private UUID sectorId;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "risk_level", nullable = false, columnDefinition = "risk_level")
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "urgency_level", nullable = false, columnDefinition = "urgency_level")
    private UrgencyLevel urgencyLevel;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false, columnDefinition = "order_status")
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "visibility", nullable = false, columnDefinition = "order_visibility")
    private OrderVisibility visibility;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "acceptance_mode", nullable = false, columnDefinition = "acceptance_mode")
    private AcceptanceMode acceptanceMode;

    @Column(name = "requirements", columnDefinition = "text")
    private String requirements;

    @Column(name = "deadline")
    private Instant deadline;

    @Column(name = "published_at")
    private Instant publishedAt;

    @Column(name = "completed_at")
    private Instant completedAt;
}
