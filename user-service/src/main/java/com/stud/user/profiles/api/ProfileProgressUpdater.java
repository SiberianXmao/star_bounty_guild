package com.stud.user.profiles.api;

import java.util.UUID;

public interface ProfileProgressUpdater {

    void recordCompletedOrder(UUID clientProfileId, UUID hunterProfileId);
}