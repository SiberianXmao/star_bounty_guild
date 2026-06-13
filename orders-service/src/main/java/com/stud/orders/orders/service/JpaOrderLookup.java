package com.stud.orders.orders.service;

import com.stud.orders.common.exception.ResourceNotFoundException;
import com.stud.orders.orders.api.OrderAcceptanceModeRef;
import com.stud.orders.orders.api.OrderLookup;
import com.stud.orders.orders.api.OrderRef;
import com.stud.orders.orders.api.OrderStatusRef;
import com.stud.orders.orders.domain.BountyOrder;
import com.stud.orders.orders.repository.BountyOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaOrderLookup implements OrderLookup {

    private final BountyOrderRepository bountyOrderRepository;

    @Override
    public OrderRef getOrder(UUID orderId) {
        return bountyOrderRepository.findById(orderId)
                .map(this::toRef)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    @Override
    public Map<UUID, OrderRef> getOrdersByIds(Collection<UUID> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Map.of();
        }

        return bountyOrderRepository.findAllById(orderIds).stream()
                .map(this::toRef)
                .collect(Collectors.toMap(OrderRef::id, ref -> ref));
    }

    private OrderRef toRef(BountyOrder order) {
        return new OrderRef(
                order.getId(),
                order.getClientId(),
                order.getAssignedHunterId(),
                order.getTitle(),
                OrderStatusRef.valueOf(order.getStatus().name()),
                OrderAcceptanceModeRef.valueOf(order.getAcceptanceMode().name())
        );
    }
}