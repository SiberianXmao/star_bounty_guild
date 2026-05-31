package com.stud.backend.applications.repository;


import com.stud.backend.applications.domain.OrderApplication;
import com.stud.backend.applications.domain.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderApplicationRepository extends JpaRepository<OrderApplication, UUID> {

    boolean existsByOrder_IdAndHunter_Id(UUID orderId, UUID hunterId);

    List<OrderApplication> findAllByOrder_Id(UUID orderId);

    List<OrderApplication> findAllByHunter_IdOrderByCreatedAtDesc(UUID hunterId);

    List<OrderApplication> findAllByOrder_IdAndStatus(UUID orderId, ApplicationStatus status);
}
