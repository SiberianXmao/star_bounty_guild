package com.stud.orders.orders.service;

import com.stud.orders.common.exception.BadRequestException;
import com.stud.orders.common.exception.ResourceNotFoundException;
import com.stud.orders.orders.api.OrderAssignmentUpdater;
import com.stud.orders.orders.domain.BountyOrder;
import com.stud.orders.orders.domain.enums.OrderStatus;
import com.stud.orders.orders.repository.BountyOrderRepository;
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