package com.stud.orders.reviews.repository;

import com.stud.orders.reviews.domain.HunterReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HunterReviewRepository extends JpaRepository<HunterReview, UUID> {

    boolean existsByOrderId(UUID orderId);

    Optional<HunterReview> findByOrderId(UUID orderId);

    List<HunterReview> findAllByClientProfileIdOrderByCreatedAtDesc(UUID clientProfileId);

    Page<HunterReview> findAllByHunterProfileIdOrderByCreatedAtDesc(UUID hunterProfileId, Pageable pageable);
}
