package com.stud.backend.profiles.client;

import com.stud.backend.profiles.api.ClientProfileRef;
import com.stud.backend.profiles.api.HunterProfileRef;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "profiles-service",
        url = "${app.services.profiles.base-url}",
        path = "/internal/v1/profiles"
)
public interface ProfileServiceFeignClient {

    @GetMapping("/client/{id}")
    ClientProfileRef getClientProfile(@PathVariable UUID id);

    @GetMapping("/hunter/{id}")
    HunterProfileRef getHunterProfile(@PathVariable UUID id);

    @GetMapping("/client/by-user/{userId}")
    ClientProfileRef getClientProfileByUserId(@PathVariable UUID userId);

    @GetMapping("/hunter/by-user/{userId}")
    HunterProfileRef getHunterProfileByUserId(@PathVariable UUID userId);

    @PostMapping("/clients/lookup")
    List<ClientProfileRef> getClientProfiles(@RequestBody UuidLookupRequest request);

    @PostMapping("/hunters/lookup")
    List<HunterProfileRef> getHunterProfiles(@RequestBody UuidLookupRequest request);

    @PostMapping("/progress/completed-order")
    void recordCompletedOrder(@RequestBody CompletedOrderProgressRequest request);

    record UuidLookupRequest(Collection<UUID> ids) {
    }

    record CompletedOrderProgressRequest(
            UUID clientProfileId,
            UUID hunterProfileId
    ) {
    }
}
