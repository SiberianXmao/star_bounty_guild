package com.stud.backend.profiles.domain;


import com.stud.backend.common.persistence.BaseUuidEntity;
import com.stud.backend.profiles.domain.enums.AvailabilityStatus;
import com.stud.backend.users.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "hunter_profiles", schema = "bounty")
public class HunterProfile extends BaseUuidEntity {

    // подумать над связями один ко многим и тд
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "callsign", nullable = false, unique = true, length = 120)
    private String callsign;

    @Column(name = "bio", columnDefinition = "text")
    private String bio;

    @Column(name = "faction_id")
    private UUID factionId;

    @Column(name = "home_planet_id")
    private UUID homePlanetId;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "availability_status", nullable = false, columnDefinition = "availability_status")
    private AvailabilityStatus availabilityStatus;

    @Column(name = "min_reward", precision = 14, scale = 2)
    private BigDecimal minReward;

    @Column(name = "reliability_score", nullable = false)
    private Integer reliabilityScore;

    @Column(name = "average_rating", nullable = false, precision = 3, scale = 2)
    private BigDecimal averageRating;

    @Column(name = "completed_orders_count", nullable = false)
    private Integer completedOrdersCount;

    @Column(name = "failed_orders_count", nullable = false)
    private Integer failedOrdersCount;
}