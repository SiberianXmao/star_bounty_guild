package com.stud.orders.reviews.domain;

import com.stud.orders.common.persistence.BaseUuidEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "hunter_reviews", schema = "bounty")
public class HunterReview extends BaseUuidEntity {

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Column(name = "client_profile_id", nullable = false)
    private UUID clientProfileId;

    @Column(name = "hunter_profile_id", nullable = false)
    private UUID hunterProfileId;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comment", length = 2000)
    private String comment;
}
