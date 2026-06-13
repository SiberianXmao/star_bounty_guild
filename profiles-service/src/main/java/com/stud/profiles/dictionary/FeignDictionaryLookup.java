package com.stud.profiles.dictionary;

import com.stud.profiles.common.exception.ResourceNotFoundException;
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
public class FeignDictionaryLookup implements DictionaryLookup {

    private final DictionaryServiceFeignClient client;

    @Override
    public FactionRef getFaction(UUID factionId) {
        return get(
                () -> client.getFaction(factionId),
                "Faction not found: " + factionId
        );
    }

    @Override
    public PlanetRef getPlanet(UUID planetId) {
        return get(
                () -> client.getPlanet(planetId),
                "Planet not found: " + planetId
        );
    }

    @Override
    public SkillRef getSkill(UUID skillId) {
        return get(
                () -> client.getSkill(skillId),
                "Skill not found: " + skillId
        );
    }

    @Override
    public Map<UUID, FactionRef> getFactionsByIds(Collection<UUID> factionIds) {
        List<UUID> ids = normalizeUuids(factionIds);
        if (ids.isEmpty()) {
            return Map.of();
        }

        return toMap(client.getFactions(new DictionaryServiceFeignClient.UuidLookupRequest(ids)), FactionRef::id);
    }

    @Override
    public Map<UUID, PlanetRef> getPlanetsById(Collection<UUID> planetIds) {
        List<UUID> ids = normalizeUuids(planetIds);
        if (ids.isEmpty()) {
            return Map.of();
        }

        return toMap(client.getPlanets(new DictionaryServiceFeignClient.UuidLookupRequest(ids)), PlanetRef::id);
    }

    @Override
    public Map<UUID, SkillRef> getSkillsById(Collection<UUID> skillIds) {
        List<UUID> ids = normalizeUuids(skillIds);
        if (ids.isEmpty()) {
            return Map.of();
        }

        return toMap(client.getSkills(new DictionaryServiceFeignClient.UuidLookupRequest(ids)), SkillRef::id);
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
