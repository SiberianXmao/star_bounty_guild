package com.stud.backend.profiles.web;


import com.stud.backend.profiles.service.ProfileService;
import com.stud.backend.profiles.web.dto.ProfileDtos;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stud.backend.profiles.web.dto.ProfileDtos.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profiles")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ProfileDtos.MyProfilesResponse getMyProfiles(Authentication authentication) {
        return profileService.getMyProfiles(authentication.getName());
    }

    @PostMapping("/client")
    public ClientProfileResponse createMyClientProfile(
            Authentication authentication,
            @Valid @RequestBody ClientProfileCreateRequest request
    ) {
        return profileService.createMyClientProfile(authentication.getName(), request);
    }

    @PostMapping("/hunter")
    public HunterProfileResponse createMyHunterProfile(
            Authentication authentication,
            @Valid @RequestBody HunterProfileCreateRequest request
    ) {
        return profileService.createMyHunterProfile(authentication.getName(), request);
    }

    @PostMapping("/hunter/skills")
    public HunterProfileResponse addSkillToMyHunterProfile(
            Authentication authentication,
            @Valid @RequestBody HunterSkillRequest request
    ) {
        return profileService.addSkillToMyHunterProfile(authentication.getName(), request);
    }

    @DeleteMapping("/hunter/skills/{skillId}")
    public HunterProfileResponse removeSkillFromMyHunterProfile(
            Authentication authentication,
            @PathVariable UUID skillId
    ) {
        return profileService.removeSkillFromMyHunterProfile(authentication.getName(), skillId);
    }

    @GetMapping("/clients")
    public List<ClientProfileResponse> getClientProfiles() {
        return profileService.getClientProfiles();
    }

    @GetMapping("/hunters")
    public List<HunterProfileResponse> getHunterProfiles() {
        return profileService.getHunterProfiles();
    }

    @GetMapping("/client/{profileId}")
    public ClientProfileResponse getClientProfile(@PathVariable UUID profileId) {
        return profileService.getClientProfile(profileId);
    }

    @GetMapping("/hunter/{profileId}")
    public HunterProfileResponse getHunterProfile(@PathVariable UUID profileId) {
        return profileService.getHunterProfile(profileId);
    }
}