package com.stud.backend.applications.domain;

import com.stud.backend.applications.domain.enums.ApplicationStatus;
import com.stud.backend.common.persistence.BaseUuidEntity;
import com.stud.backend.orders.domain.BountyOrder;
import com.stud.backend.profiles.domain.HunterProfile;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(
        name = "order_applications",
        schema = "bounty",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_order_applications_order_hunter",
                        columnNames = {"order_id", "hunter_id"}
                )
        }
)
public class OrderApplication extends BaseUuidEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private BountyOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hunter_id", insertable = false, updatable = false)
    private HunterProfile hunter;

    @Column(name = "hunter_id", nullable = false)
    private UUID hunterId;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @Column(name = "proposed_reward", precision = 14, scale = 2)
    private BigDecimal proposedReward;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false, columnDefinition = "application_status")
    private ApplicationStatus status;
}