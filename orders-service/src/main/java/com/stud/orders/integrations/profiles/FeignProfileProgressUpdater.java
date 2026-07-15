package com.stud.orders.integrations.profiles;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeignProfileProgressUpdater implements ProfileProgressUpdater {

    private final ProfileServiceFeignClient client;

    @Override
    public void recordCompletedOrder(UUID clientProfileId, UUID hunterProfileId) {
        client.recordCompletedOrder(
                new ProfileServiceFeignClient.CompletedOrderProgressRequest(clientProfileId, hunterProfileId)
        );
    }
}
