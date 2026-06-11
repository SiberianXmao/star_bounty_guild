package com.stud.backend.orders.service;

import com.stud.backend.common.exception.BadRequestException;
import com.stud.backend.common.exception.ResourceNotFoundException;
import com.stud.backend.orders.api.OrderAssignmentUpdater;
import com.stud.backend.orders.domain.BountyOrder;
import com.stud.backend.orders.domain.enums.OrderStatus;
import com.stud.backend.orders.repository.BountyOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class JpaOrderAssignmentUpdater implements OrderAssignmentUpdater {

    private final BountyOrderRepository bountyOrderRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void assignHunter(UUID orderId, UUID hunterProfileId) {
        BountyOrder order = bountyOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (Objects.equals(order.getAssignedHunterId(), hunterProfileId)
                && order.getStatus() != OrderStatus.OPEN) {
            return;
        }

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new BadRequestException("Order cannot be assigned in current status: " + order.getStatus());
        }

        order.setAssignedHunterId(hunterProfileId);
        order.setStatus(OrderStatus.ASSIGNED);
    }
}