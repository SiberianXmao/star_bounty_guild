package com.stud.backend.orders.api;


import java.util.UUID;

public record OrderRef(
        UUID id,
        UUID clientId,
        UUID assignedHunterId,
        String title,
        OrderStatusRef status,
        OrderAcceptanceModeRef acceptanceMode
) {
    public boolean isOpen() {
        return status == OrderStatusRef.OPEN;
    }

    public boolean acceptsApplications() {
        return acceptanceMode == OrderAcceptanceModeRef.APPLICATIONS;
    }
}
