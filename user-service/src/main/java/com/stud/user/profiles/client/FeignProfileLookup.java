package com.stud.user.profiles.client;

import com.stud.user.common.exception.ResourceNotFoundException;
import com.stud.user.profiles.api.ClientProfileRef;
import com.stud.user.profiles.api.HunterProfileRef;
import com.stud.user.profiles.api.ProfileLookup;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeignProfileLookup implements ProfileLookup {

    private final ProfileServiceFeignClient client;

    @Override
    public ClientProfileRef getClientProfile(UUID clientProfileId) {
        return get(
                () -> client.getClientProfile(clientProfileId),
                "Client profile not found: " + clientProfileId
        );
    }

    @Override
    public HunterProfileRef getHunterProfile(UUID hunterProfileId) {
        return get(
                () -> client.getHunterProfile(hunterProfileId),
                "Hunter profile not found: " + hunterProfileId
        );
    }

    @Override
    public ClientProfileRef getClientProfileByUserId(UUID userId) {
        return get(
                () -> client.getClientProfileByUserId(userId),
                "Client profile not found for user: " + userId
        );
    }

    @Override
    public HunterProfileRef getHunterProfileByUserId(UUID userId) {
        return get(
                () -> client.getHunterProfileByUserId(userId),
                "Hunter profile not found for user: " + userId
        );
    }

    @Override
    public Map<UUID, ClientProfileRef> getClientProfilesByIds(Collection<UUID> clientProfileIds) {
        List<UUID> ids = normalizeUuids(clientProfileIds);
        if (ids.isEmpty()) {
            return Map.of();
        }

        return toMap(client.getClientProfiles(new ProfileServiceFeignClient.UuidLookupRequest(ids)), ClientProfileRef::id);
    }

    @Override
    public Map<UUID, HunterProfileRef> getHunterProfilesByIds(Collection<UUID> hunterProfileIds) {
        List<UUID> ids = normalizeUuids(hunterProfileIds);
        if (ids.isEmpty()) {
            return Map.of();
        }

        return toMap(client.getHunterProfiles(new ProfileServiceFeignClient.UuidLookupRequest(ids)), HunterProfileRef::id);
    }

    private <T> T get(Supplier<T> request, String notFoundMessage) {
        try {
            return request.get();
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException(notFoundMessage);
        }
    }

    private List<UUID> normalizeUuids(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        return ids.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private <K, V> Map<K, V> toMap(List<V> refs, Function<V, K> keyExtractor) {
        if (refs == null || refs.isEmpty()) {
            return Map.of();
        }

        return refs.stream()
                .collect(Collectors.toMap(keyExtractor, Function.identity()));
    }
}
