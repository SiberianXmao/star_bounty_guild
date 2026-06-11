package com.stud.backend.profiles.domain;


import com.stud.backend.common.persistence.BaseUuidEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client_profiles", schema = "bounty")
public class ClientProfile extends BaseUuidEntity {

    // подумать над связями один ко многим и тд

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    //---
    @Column(name = "faction_id")
    private UUID factionId;

    //---
    @Column(name = "planet_id")
    private UUID planetId;

    @Column(name = "reliability_score", nullable = false)
    private Integer reliabilityScore;

    @Column(name = "average_rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "completed_orders_count", nullable = false)
    private Integer completedOrdersCount;

    @Column(name = "cancelled_orders_count", nullable = false)
    private Integer cancelledOrdersCount;
}