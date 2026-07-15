package com.stud.orders.integrations.profiles;

import java.util.UUID;

public interface ProfileProgressUpdater {

    void recordCompletedOrder(UUID clientProfileId, UUID hunterProfileId);
}
