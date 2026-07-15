package com.stud.orders.integrations.profiles;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface ProfileLookup {

    ClientProfileRef getClientProfile(UUID clientProfileId);

    HunterProfileRef getHunterProfile(UUID hunterProfileId);

    ClientProfileRef getClientProfileByUserId(UUID userId);

    HunterProfileRef getHunterProfileByUserId(UUID userId);

    Map<UUID, ClientProfileRef> getClientProfilesByIds(Collection<UUID> clientProfileIds);

    Map<UUID, HunterProfileRef> getHunterProfilesByIds(Collection<UUID> hunterProfileIds);
}
