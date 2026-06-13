package com.stud.profiles.internal;

import com.stud.profiles.internal.dto.ProfileInternalDtos.ClientProfileRef;
import com.stud.profiles.internal.dto.ProfileInternalDtos.CompletedOrderProgressRequest;
import com.stud.profiles.internal.dto.ProfileInternalDtos.HunterProfileRef;
import com.stud.profiles.internal.dto.ProfileInternalDtos.UuidLookupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/profiles")
public class InternalProfileController {

    private final ProfileLookupService profileLookupService;

    @GetMapping("/client/{id}")
    public ClientProfileRef getClientProfile(@PathVariable UUID id) {
        return profileLookupService.getClientProfile(id);
    }

    @GetMapping("/hunter/{id}")
    public HunterProfileRef getHunterProfile(@PathVariable UUID id) {
        return profileLookupService.getHunterProfile(id);
    }

    @GetMapping("/client/by-user/{userId}")
    public ClientProfileRef getClientProfileByUserId(@PathVariable UUID userId) {
        return profileLookupService.getClientProfileByUserId(userId);
    }

    @GetMapping("/hunter/by-user/{userId}")
    public HunterProfileRef getHunterProfileByUserId(@PathVariable UUID userId) {
        return profileLookupService.getHunterProfileByUserId(userId);
    }

    @PostMapping("/clients/lookup")
    public List<ClientProfileRef> getClientProfiles(@RequestBody UuidLookupRequest request) {
        return profileLookupService.getClientProfiles(request.ids());
    }

    @PostMapping("/hunters/lookup")
    public List<HunterProfileRef> getHunterProfiles(@RequestBody UuidLookupRequest request) {
        return profileLookupService.getHunterProfiles(request.ids());
    }

    @PostMapping("/progress/completed-order")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void recordCompletedOrder(@RequestBody CompletedOrderProgressRequest request) {
        profileLookupService.recordCompletedOrder(request.clientProfileId(), request.hunterProfileId());
    }
}
