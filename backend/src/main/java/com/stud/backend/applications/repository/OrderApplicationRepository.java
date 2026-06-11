package com.stud.backend.applications.repository;


import com.stud.backend.applications.domain.OrderApplication;
import com.stud.backend.applications.domain.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderApplicationRepository extends JpaRepository<OrderApplication, UUID> {

    boolean existsByOrder_IdAndHunterId(UUID orderId, UUID hunterId);

    @Override
    @EntityGraph(attributePaths = {"order", "hunter"})
    Optional<OrderApplication> findById(UUID id);

    @EntityGraph(attributePaths = {"order", "hunter"})
    List<OrderApplication> findAllByOrder_Id(UUID orderId);

    @EntityGraph(attributePaths = {"order", "hunter"})
    List<OrderApplication> findAllByHunterIdOrderByCreatedAtDesc(UUID hunterId);

    @EntityGraph(attributePaths = {"order", "hunter"})
    List<OrderApplication> findAllByOrder_IdAndStatus(UUID orderId, ApplicationStatus status);
}
