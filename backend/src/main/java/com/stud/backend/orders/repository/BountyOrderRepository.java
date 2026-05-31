package com.stud.backend.orders.repository;

import com.stud.backend.orders.domain.BountyOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BountyOrderRepository extends JpaRepository<BountyOrder, UUID>, JpaSpecificationExecutor<BountyOrder> {

    Page<BountyOrder> findAllByClientId(UUID clientId, Pageable pageable);

    Page<BountyOrder> findAllByAssignedHunterId(UUID hunterId, Pageable pageable);
}