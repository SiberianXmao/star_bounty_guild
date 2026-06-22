package com.stud.orders.orders.api;

import java.util.UUID;

public interface OrderAssignmentUpdater {

    void assignHunter(UUID orderId, UUID hunterProfileId);
}