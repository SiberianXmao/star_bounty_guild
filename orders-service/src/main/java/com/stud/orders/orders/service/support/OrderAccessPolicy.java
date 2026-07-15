package com.stud.orders.orders.service.support;

import com.stud.orders.common.exception.BadRequestException;
import com.stud.orders.common.exception.ResourceNotFoundException;
import com.stud.orders.integrations.profiles.ClientProfileRef;
import com.stud.orders.integrations.profiles.HunterProfileRef;
import com.stud.orders.orders.domain.BountyOrder;
import com.stud.orders.orders.domain.enums.AcceptanceMode;
import com.stud.orders.orders.domain.enums.OrderStatus;
import com.stud.orders.orders.domain.enums.OrderVisibility;
import org.springframework.stereotype.Component;

import java.util.UUID;

// проверки владельца, назначенного охотника, статусов и публичной видимости

@Component
public class OrderAccessPolicy {

    public void ensureOwner(BountyOrder order, ClientProfileRef clientProfile) {
        if (!order.getClientId().equals(clientProfile.id())) {
            throw new ResourceNotFoundException("Order not found: " + order.getId());
        }
    }

    public void ensureAssignedHunter(BountyOrder order, HunterProfileRef hunterProfile) {
        if (order.getAssignedHunterId() == null || !order.getAssignedHunterId().equals(hunterProfile.id())) {
            throw new ResourceNotFoundException("Order not found: " + order.getId());
        }
    }

    public void ensureDraft(BountyOrder order) {
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT orders can be changed by this action");
        }
    }

    public void ensurePublishable(BountyOrder order) {
        if (order.getVisibility() != OrderVisibility.PUBLIC) {
            throw new BadRequestException("Only PUBLIC orders can be published to the board in this module");
        }

        if (order.getAcceptanceMode() == AcceptanceMode.PERSONAL_OFFER) {
            throw new BadRequestException("PERSONAL_OFFER orders will be handled in offers module");
        }
    }

    public void ensureCancellable(BountyOrder order) {
        if (order.getStatus() != OrderStatus.DRAFT && order.getStatus() != OrderStatus.OPEN) {
            throw new BadRequestException("Only DRAFT or OPEN orders can be cancelled now");
        }
    }

    public void ensureCanStart(BountyOrder order) {
        if (order.getStatus() != OrderStatus.ASSIGNED) {
            throw new BadRequestException("Only ASSIGNED orders can be started");
        }
    }

    public void ensureCanSubmit(BountyOrder order) {
        if (order.getStatus() != OrderStatus.IN_PROGRESS) {
            throw new BadRequestException("Only IN_PROGRESS orders can be submitted");
        }
    }

    public void ensureCanComplete(BountyOrder order) {
        if (order.getStatus() != OrderStatus.SUBMITTED) {
            throw new BadRequestException("Only SUBMITTED orders can be completed");
        }
    }

    public UUID requireAssignedHunterId(BountyOrder order) {
        UUID hunterProfileId = order.getAssignedHunterId();

        if (hunterProfileId == null) {
            throw new BadRequestException("Order has no assigned hunter");
        }

        return hunterProfileId;
    }

    public void ensurePublicBoardVisible(BountyOrder order, UUID orderId) {
        if (order.getVisibility() != OrderVisibility.PUBLIC || order.getStatus() != OrderStatus.OPEN) {
            throw new ResourceNotFoundException("Order not found: " + orderId);
        }
    }
}
