package com.stud.backend.orders.repository;

import com.stud.backend.orders.domain.BountyOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface BountyOrderRepository extends JpaRepository<BountyOrder, UUID>, JpaSpecificationExecutor<BountyOrder> {

    @Override
    Optional<BountyOrder> findById(UUID id);

    @Override
    Page<BountyOrder> findAll(Specification<BountyOrder> spec, Pageable pageable);

    Page<BountyOrder> findAllByClientId(UUID clientId, Pageable pageable);

    Page<BountyOrder> findAllByAssignedHunterId(UUID hunterId, Pageable pageable);
}
