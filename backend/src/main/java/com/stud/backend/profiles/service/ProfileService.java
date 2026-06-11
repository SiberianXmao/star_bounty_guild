package com.stud.backend.profiles.service;


import com.stud.backend.common.exception.DuplicateResourceException;
import com.stud.backend.common.exception.ResourceNotFoundException;

import com.stud.backend.dictionary.api.DictionaryLookup;
import com.stud.backend.dictionary.api.FactionRef;
import com.stud.backend.dictionary.api.PlanetRef;
import com.stud.backend.dictionary.api.SkillRef;

import com.stud.backend.profiles.domain.ClientProfile;
import com.stud.backend.profiles.domain.HunterProfile;
import com.stud.backend.profiles.domain.HunterSkill;
import com.stud.backend.profiles.domain.HunterSkillId;
import com.stud.backend.profiles.domain.enums.AvailabilityStatus;

import com.stud.backend.profiles.repository.ClientProfileRepository;
import com.stud.backend.profiles.repository.HunterProfileRepository;
import com.stud.backend.profiles.repository.HunterSkillRepository;

import com.stud.backend.users.api.UserLookup;
import com.stud.backend.users.api.UserRef;

import com.stud.backend.users.api.UserRoleManager;
import com.stud.backend.users.api.UserRoleNameRef;

import com.stud.backend.profiles.web.dto.ProfileDtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRoleManager userRoleManager;
    private final DictionaryLookup dictionaryLookup;
    private final UserLookup userLookup;

    private final ClientProfileRepository clientProfileRepository;
    private final HunterProfileRepository hunterProfileRepository;
    private final HunterSkillRepository hunterSkillRepository;

    public MyProfilesResponse getMyProfiles(String email) {
        UserRef user = findUserByEmail(email);

        ClientProfileResponse clientProfile = clientProfileRepository.findByUserId(user.id())
                .map(this::toClientProfileResponse)
                .orElse(null);

        HunterProfileResponse hunterProfile = hunterProfileRepository.findByUserId(user.id())
                .map(this::toHunterProfileResponse)
                .orElse(null);

        return new MyProfilesResponse(clientProfile, hunterProfile);
    }

    public List<ClientProfileResponse> getClientProfiles() {
        List<ClientProfile> profiles = clientProfileRepository.findAll(Sort.by("name"));

        Map<UUID,FactionRef> factionsById = dictionaryLookup.getFactionsByIds(
                profiles.stream()
                        .map(ClientProfile::getFactionId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );

        Map<UUID,PlanetRef> planetsById = dictionaryLookup.getPlanetsById(
                profiles.stream()
                        .map(ClientProfile::getPlanetId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );

        return profiles.stream()
                .map(profile -> toClientProfileResponse(profile, factionsById,planetsById))
                .toList();
    }

    public List<HunterProfileResponse> getHunterProfiles() {
        List<HunterProfile> profiles = hunterProfileRepository.findAll(Sort.by("callsign"));

        Map<UUID,FactionRef> factionsById = dictionaryLookup.getFactionsByIds(
                profiles.stream()
                        .map(HunterProfile::getFactionId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );

        Map<UUID,PlanetRef> planetsById = dictionaryLookup.getPlanetsById(
                profiles.stream()
                        .map(HunterProfile::getHomePlanetId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );

        Map<UUID,List<HunterSkillResponse>> skillsByHunterProfileId = getSkillsByHunterProfileId(profiles);

        return profiles.stream()
                .map(profile -> toHunterProfileResponse(
                        profile,
                        skillsByHunterProfileId.getOrDefault(profile.getId(),List.of()),
                        factionsById,
                        planetsById
                ))
                .toList();
    }

    public ClientProfileResponse getClientProfile(UUID profileId) {
        return toClientProfileResponse(findClientProfile(profileId));
    }

    public HunterProfileResponse getHunterProfile(UUID profileId) {
        return toHunterProfileResponse(findHunterProfile(profileId));
    }

    @Transactional
    public ClientProfileResponse createMyClientProfile(String email, ClientProfileCreateRequest request) {
        UserRef user = findUserByEmail(email);

        if (clientProfileRepository.existsByUserId(user.id())) {
            throw new DuplicateResourceException("Client profile already exists for current user");
        }

        ClientProfile profile = new ClientProfile();
        profile.setUserId(user.id());
        profile.setName(request.name().trim());
        profile.setDescription(request.description());
        profile.setReliabilityScore(50);
        profile.setAverageRating(BigDecimal.ZERO);
        profile.setCompletedOrdersCount(0);
        profile.setCancelledOrdersCount(0);

        if (request.factionId() != null) {
            profile.setFactionId(findFaction(request.factionId()).id());
        }

        if (request.planetId() != null) {
            profile.setPlanetId(findPlanet(request.planetId()).id());
        }

        ClientProfile savedProfile = clientProfileRepository.save(profile);
        userRoleManager.ensureRole(user.id(), UserRoleNameRef.CLIENT);

        return toClientProfileResponse(savedProfile);
    }

    @Transactional
    public HunterProfileResponse createMyHunterProfile(String email, HunterProfileCreateRequest request) {
        UserRef user = findUserByEmail(email);
        String callsign = request.callsign().trim();

        if (hunterProfileRepository.existsByUserId(user.id())) {
            throw new DuplicateResourceException("Hunter profile already exists for current user");
        }

        if (hunterProfileRepository.existsByCallsignIgnoreCase(callsign)) {
            throw new DuplicateResourceException("Hunter profile with callsign '%s' already exists".formatted(callsign));
        }

        HunterProfile profile = new HunterProfile();
        profile.setUserId(user.id());
        profile.setCallsign(callsign);
        profile.setBio(request.bio());
        profile.setAvailabilityStatus(
                request.availabilityStatus() == null
                        ? AvailabilityStatus.AVAILABLE
                        : request.availabilityStatus()
        );
        profile.setMinReward(request.minReward());
        profile.setReliabilityScore(50);
        profile.setAverageRating(BigDecimal.ZERO);
        profile.setCompletedOrdersCount(0);
        profile.setFailedOrdersCount(0);

        if (request.factionId() != null) {
            profile.setFactionId(findFaction(request.factionId()).id());
        }

        if (request.homePlanetId() != null) {
            profile.setHomePlanetId(findPlanet(request.homePlanetId()).id());
        }

        HunterProfile savedProfile = hunterProfileRepository.save(profile);
        userRoleManager.ensureRole(user.id(), UserRoleNameRef.HUNTER);

        return toHunterProfileResponse(savedProfile);
    }

    @Transactional
    public HunterProfileResponse addSkillToMyHunterProfile(String email, HunterSkillRequest request) {
        UserRef user = findUserByEmail(email);

        HunterProfile hunterProfile = hunterProfileRepository.findByUserId(user.id())
                .orElseThrow(() -> new ResourceNotFoundException("Hunter profile not found for current user"));

        SkillRef skill = findSkill(request.skillId());

        HunterSkillId id = new HunterSkillId(hunterProfile.getId(), skill.id());

        HunterSkill hunterSkill = hunterSkillRepository.findById(id)
                .orElseGet(() -> new HunterSkill(hunterProfile, skill.id(), request.level()));

        hunterSkill.setLevel(request.level());
        hunterSkillRepository.save(hunterSkill);

        return toHunterProfileResponse(hunterProfile);
    }

    @Transactional
    public HunterProfileResponse removeSkillFromMyHunterProfile(String email, UUID skillId) {
        UserRef user = findUserByEmail(email);

        HunterProfile hunterProfile = hunterProfileRepository.findByUserId(user.id())
                .orElseThrow(() -> new ResourceNotFoundException("Hunter profile not found for current user"));

        HunterSkillId id = new HunterSkillId(hunterProfile.getId(), skillId);
        hunterSkillRepository.deleteById(id);

        return toHunterProfileResponse(hunterProfile);
    }

    private UserRef findUserByEmail(String email) {
        return userLookup.getByEmail(email);
    }

    private ClientProfile findClientProfile(UUID profileId) {
        return clientProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found: " + profileId));
    }

    private HunterProfile findHunterProfile(UUID profileId) {
        return hunterProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Hunter profile not found: " + profileId));
    }

    private FactionRef findFaction(UUID factionId) {
        return dictionaryLookup.getFaction(factionId);
    }

    private PlanetRef findPlanet(UUID planetId) {
        return dictionaryLookup.getPlanet(planetId);
    }

    private SkillRef findSkill(UUID skillId) {
        return dictionaryLookup.getSkill(skillId);
    }

    private ClientProfileResponse toClientProfileResponse(ClientProfile profile) {
        FactionRef faction = profile.getFactionId() == null ? null : findFaction(profile.getFactionId());
        PlanetRef planet = profile.getPlanetId() == null ? null : findPlanet(profile.getPlanetId());

        return new ClientProfileResponse(
                profile.getId(),
                profile.getUserId(),
                profile.getName(),
                profile.getDescription(),
                faction == null ? null : faction.id(),
                faction == null ? null : faction.name(),
                planet == null ? null : planet.id(),
                planet == null ? null : planet.name(),
                profile.getReliabilityScore(),
                profile.getAverageRating(),
                profile.getCompletedOrdersCount(),
                profile.getCancelledOrdersCount()
        );
    }

    private ClientProfileResponse toClientProfileResponse(
            ClientProfile profile,
            Map<UUID, FactionRef> factionsById,
            Map<UUID, PlanetRef> planetsById
    ) {
        FactionRef faction = profile.getFactionId() == null
                ? null
                : factionsById.get(profile.getFactionId());

        PlanetRef planet = profile.getPlanetId() == null
                ? null
                : planetsById.get(profile.getPlanetId());

        return new ClientProfileResponse(
                profile.getId(),
                profile.getUserId(),
                profile.getName(),
                profile.getDescription(),
                faction == null ? null : faction.id(),
                faction == null ? null : faction.name(),
                planet == null ? null : planet.id(),
                planet == null ? null : planet.name(),
                profile.getReliabilityScore(),
                profile.getAverageRating(),
                profile.getCompletedOrdersCount(),
                profile.getCancelledOrdersCount()
        );
    }

    private HunterProfileResponse toHunterProfileResponse(HunterProfile profile) {
        List<HunterSkillResponse> skills = hunterSkillRepository.findAllByHunterProfileId(profile.getId())
                .stream()
                .map(this::toHunterSkillResponse)
                .toList();

        return toHunterProfileResponse(profile, skills);
    }

    private HunterProfileResponse toHunterProfileResponse(HunterProfile profile, List<HunterSkillResponse> skills) {
        FactionRef faction = profile.getFactionId() == null ? null : findFaction(profile.getFactionId());
        PlanetRef homePlanet = profile.getHomePlanetId() == null ? null : findPlanet(profile.getHomePlanetId());

        return new HunterProfileResponse(
                profile.getId(),
                profile.getUserId(),
                profile.getCallsign(),
                profile.getBio(),
                faction == null ? null : faction.id(),
                faction == null ? null : faction.name(),
                homePlanet == null ? null : homePlanet.id(),
                homePlanet == null ? null : homePlanet.name(),
                profile.getAvailabilityStatus(),
                profile.getMinReward(),
                profile.getReliabilityScore(),
                profile.getAverageRating(),
                profile.getCompletedOrdersCount(),
                profile.getFailedOrdersCount(),
                skills
        );
    }

    private Map<UUID, List<HunterSkillResponse>> getSkillsByHunterProfileId(List<HunterProfile> profiles) {
        if (profiles.isEmpty()) {
            return Map.of();
        }

        List<UUID> hunterProfileIds = profiles.stream()
                .map(HunterProfile::getId)
                .toList();

        List<HunterSkill> hunterSkills = hunterSkillRepository.findAllByHunterProfile_IdIn(hunterProfileIds);

        Map<UUID, SkillRef> skillsById = dictionaryLookup.getSkillsById(
                hunterSkills.stream()
                        .map(hunterSkill -> hunterSkill.getId().getSkillId())
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );

        return hunterSkills.stream()
                .collect(Collectors.groupingBy(
                        hunterSkill -> hunterSkill.getId().getHunterProfileId(),
                        Collectors.mapping(
                                hunterSkill -> toHunterSkillResponse(hunterSkill, skillsById),
                                Collectors.toList()
                        )
                ));
    }

    private HunterSkillResponse toHunterSkillResponse(HunterSkill hunterSkill) {
        SkillRef skill = findSkill(hunterSkill.getId().getSkillId());

        return new HunterSkillResponse(
                skill.id(),
                skill.name(),
                hunterSkill.getLevel()
        );
    }

    private HunterSkillResponse toHunterSkillResponse(
            HunterSkill hunterSkill,
            Map<UUID, SkillRef> skillsById
    ) {
        UUID skillId = hunterSkill.getId().getSkillId();
        SkillRef skill = skillsById.get(skillId);

        return new HunterSkillResponse(
                skillId,
                skill == null ? null : skill.name(),
                hunterSkill.getLevel()
        );
    }

    private HunterProfileResponse toHunterProfileResponse(
            HunterProfile profile,
            List<HunterSkillResponse> skills,
            Map<UUID, FactionRef> factionsById,
            Map<UUID, PlanetRef> planetsById
    ) {
        FactionRef faction = profile.getFactionId() == null
                ? null
                : factionsById.get(profile.getFactionId());

        PlanetRef homePlanet = profile.getHomePlanetId() == null
                ? null
                : planetsById.get(profile.getHomePlanetId());

        return new HunterProfileResponse(
                profile.getId(),
                profile.getUserId(),
                profile.getCallsign(),
                profile.getBio(),
                faction == null ? null : faction.id(),
                faction == null ? null : faction.name(),
                homePlanet == null ? null : homePlanet.id(),
                homePlanet == null ? null : homePlanet.name(),
                profile.getAvailabilityStatus(),
                profile.getMinReward(),
                profile.getReliabilityScore(),
                profile.getAverageRating(),
                profile.getCompletedOrdersCount(),
                profile.getFailedOrdersCount(),
                skills
        );
    }
}
