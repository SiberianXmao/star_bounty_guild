package com.stud.backend.profiles.service;

import com.stud.backend.common.exception.ResourceNotFoundException;
import com.stud.backend.profiles.api.ClientProfileRef;
import com.stud.backend.profiles.api.HunterProfileRef;
import com.stud.backend.profiles.api.ProfileLookup;
import com.stud.backend.profiles.domain.ClientProfile;
import com.stud.backend.profiles.domain.HunterProfile;
import com.stud.backend.profiles.repository.ClientProfileRepository;
import com.stud.backend.profiles.repository.HunterProfileRepository;
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
public class JpaProfileLookup implements ProfileLookup {

    private final ClientProfileRepository clientProfileRepository;
    private final HunterProfileRepository hunterProfileRepository;

    @Override
    public ClientProfileRef getClientProfile(UUID clientProfileId) {
        return clientProfileRepository.findById(clientProfileId)
                .map(this::toClientRef)
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found: " + clientProfileId));
    }

    @Override
    public HunterProfileRef getHunterProfile(UUID hunterProfileId) {
        return hunterProfileRepository.findById(hunterProfileId)
                .map(this::toHunterRef)
                .orElseThrow(() -> new ResourceNotFoundException("Hunter profile not found: " + hunterProfileId));
    }

    @Override
    public ClientProfileRef getClientProfileByUserId(UUID userId) {
        return clientProfileRepository.findByUserId(userId)
                .map(this::toClientRef)
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found for user: " + userId));
    }

    @Override
    public HunterProfileRef getHunterProfileByUserId(UUID userId) {
        return hunterProfileRepository.findByUserId(userId)
                .map(this::toHunterRef)
                .orElseThrow(() -> new ResourceNotFoundException("Hunter profile not found for user: " + userId));
    }

    @Override
    public Map<UUID, ClientProfileRef> getClientProfilesByIds(Collection<UUID> clientProfileIds) {
        if (clientProfileIds == null || clientProfileIds.isEmpty()) {
            return Map.of();
        }

        return clientProfileRepository.findByIdIn(clientProfileIds).stream()
                .map(this::toClientRef)
                .collect(Collectors.toMap(ClientProfileRef::id, ref -> ref));
    }

    @Override
    public Map<UUID, HunterProfileRef> getHunterProfilesByIds(Collection<UUID> hunterProfileIds) {
       if (hunterProfileIds == null || hunterProfileIds.isEmpty()) {
           return Map.of();
       }

        return hunterProfileRepository.findByIdIn(hunterProfileIds).stream()
                .map(this::toHunterRef)
                .collect(Collectors.toMap(HunterProfileRef::id, ref -> ref));
    }

    private ClientProfileRef toClientRef(ClientProfile profile) {
        return new ClientProfileRef(
                profile.getId(),
                profile.getUserId(),
                profile.getName(),
                profile.getAverageRating(),
                profile.getReliabilityScore()
        );
    }

    private HunterProfileRef toHunterRef(HunterProfile profile) {
        return new HunterProfileRef(
                profile.getId(),
                profile.getUserId(),
                profile.getCallsign(),
                profile.getAverageRating(),
                profile.getReliabilityScore()
        );
    }
}
