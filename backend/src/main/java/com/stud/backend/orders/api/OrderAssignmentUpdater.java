package com.stud.backend.orders.api;

import java.util.UUID;

public interface OrderAssignmentUpdater {

    void assignHunter(UUID orderId, UUID hunterProfileId);
}