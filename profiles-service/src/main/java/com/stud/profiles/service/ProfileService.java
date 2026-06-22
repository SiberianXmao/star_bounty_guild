package com.stud.profiles.service;

import com.stud.profiles.web.dto.ProfileDtos.ClientProfileCreateRequest;
import com.stud.profiles.web.dto.ProfileDtos.ClientProfileResponse;
import com.stud.profiles.web.dto.ProfileDtos.HunterProfileCreateRequest;
import com.stud.profiles.web.dto.ProfileDtos.HunterProfileResponse;
import com.stud.profiles.web.dto.ProfileDtos.HunterSkillRequest;
import com.stud.profiles.web.dto.ProfileDtos.MyProfilesResponse;

import java.util.List;
import java.util.UUID;

public interface ProfileService {

    MyProfilesResponse getMyProfiles(String email);

    List<ClientProfileResponse> getClientProfiles();

    List<HunterProfileResponse> getHunterProfiles();

    ClientProfileResponse getClientProfile(UUID profileId);

    HunterProfileResponse getHunterProfile(UUID profileId);

    ClientProfileResponse createMyClientProfile(String email, ClientProfileCreateRequest request);

    HunterProfileResponse createMyHunterProfile(String email, HunterProfileCreateRequest request);

    HunterProfileResponse addSkillToMyHunterProfile(String email, HunterSkillRequest request);

    HunterProfileResponse removeSkillFromMyHunterProfile(String email, UUID skillId);
}
