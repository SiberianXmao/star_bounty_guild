package com.stud.backend.profiles.service;

import com.stud.backend.common.exception.ResourceNotFoundException;
import com.stud.backend.profiles.api.ProfileProgressUpdater;
import com.stud.backend.profiles.domain.ClientProfile;
import com.stud.backend.profiles.domain.HunterProfile;
import com.stud.backend.profiles.repository.ClientProfileRepository;
import com.stud.backend.profiles.repository.HunterProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class JpaProfileProgressUpdater implements ProfileProgressUpdater {

    private final ClientProfileRepository clientProfileRepository;
    private final HunterProfileRepository hunterProfileRepository;

    @Override
    public void recordCompletedOrder(UUID clientProfileId, UUID hunterProfileId) {
        ClientProfile client = clientProfileRepository.findById(clientProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found: " + clientProfileId));

        HunterProfile hunter = hunterProfileRepository.findById(hunterProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("Hunter profile not found: " + hunterProfileId));

        client.setCompletedOrdersCount(client.getCompletedOrdersCount() + 1);
        hunter.setCompletedOrdersCount(hunter.getCompletedOrdersCount() + 1);
    }
}
