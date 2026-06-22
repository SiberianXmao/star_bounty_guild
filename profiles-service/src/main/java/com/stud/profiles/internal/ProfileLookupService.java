package com.stud.profiles.internal;

import com.stud.profiles.common.exception.ResourceNotFoundException;
import com.stud.profiles.domain.ClientProfile;
import com.stud.profiles.domain.HunterProfile;
import com.stud.profiles.internal.dto.ProfileInternalDtos.ClientProfileRef;
import com.stud.profiles.internal.dto.ProfileInternalDtos.HunterProfileRef;
import com.stud.profiles.repository.ClientProfileRepository;
import com.stud.profiles.repository.HunterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileLookupService {

    private final ClientProfileRepository clientProfileRepository;
    private final HunterProfileRepository hunterProfileRepository;

    public ClientProfileRef getClientProfile(UUID clientProfileId) {
        return clientProfileRepository.findById(clientProfileId)
                .map(this::toClientRef)
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found: " + clientProfileId));
    }

    public HunterProfileRef getHunterProfile(UUID hunterProfileId) {
        return hunterProfileRepository.findById(hunterProfileId)
                .map(this::toHunterRef)
                .orElseThrow(() -> new ResourceNotFoundException("Hunter profile not found: " + hunterProfileId));
    }

    public ClientProfileRef getClientProfileByUserId(UUID userId) {
        return clientProfileRepository.findByUserId(userId)
                .map(this::toClientRef)
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found for user: " + userId));
    }

    public HunterProfileRef getHunterProfileByUserId(UUID userId) {
        return hunterProfileRepository.findByUserId(userId)
                .map(this::toHunterRef)
                .orElseThrow(() -> new ResourceNotFoundException("Hunter profile not found for user: " + userId));
    }

    public List<ClientProfileRef> getClientProfiles(Collection<UUID> clientProfileIds) {
        if (clientProfileIds == null || clientProfileIds.isEmpty()) {
            return List.of();
        }

        return clientProfileRepository.findByIdIn(clientProfileIds).stream()
                .map(this::toClientRef)
                .toList();
    }

    public List<HunterProfileRef> getHunterProfiles(Collection<UUID> hunterProfileIds) {
        if (hunterProfileIds == null || hunterProfileIds.isEmpty()) {
            return List.of();
        }

        return hunterProfileRepository.findByIdIn(hunterProfileIds).stream()
                .map(this::toHunterRef)
                .toList();
    }

    @Transactional
    public void recordCompletedOrder(UUID clientProfileId, UUID hunterProfileId) {
        ClientProfile client = clientProfileRepository.findById(clientProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found: " + clientProfileId));

        HunterProfile hunter = hunterProfileRepository.findById(hunterProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Hunter profile not found: " + hunterProfileId));

        client.setCompletedOrdersCount(client.getCompletedOrdersCount() + 1);
        hunter.setCompletedOrdersCount(hunter.getCompletedOrdersCount() + 1);
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
