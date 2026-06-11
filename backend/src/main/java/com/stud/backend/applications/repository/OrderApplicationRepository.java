package com.stud.backend.applications.repository;


import com.stud.backend.applications.domain.OrderApplication;
import com.stud.backend.applications.domain.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderApplicationRepository extends JpaRepository<OrderApplication, UUID> {

    boolean existsByOrderIdAndHunterId(UUID orderId, UUID hunterId);

    @Override
    Optional<OrderApplication> findById(UUID id);

    List<OrderApplication> findAllByOrderId(UUID orderId);

    List<OrderApplication> findAllByHunterIdOrderByCreatedAtDesc(UUID hunterId);

    List<OrderApplication> findAllByOrderIdAndStatus(UUID orderId, ApplicationStatus status);
}
