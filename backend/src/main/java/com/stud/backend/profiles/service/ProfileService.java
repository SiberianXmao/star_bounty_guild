package com.stud.backend.profiles.service;


import com.stud.backend.common.exception.DuplicateResourceException;
import com.stud.backend.common.exception.ResourceNotFoundException;
import com.stud.backend.dictionary.domain.Faction;
import com.stud.backend.dictionary.domain.Planet;
import com.stud.backend.dictionary.domain.Skill;
import com.stud.backend.dictionary.repository.FactionRepository;
import com.stud.backend.dictionary.repository.PlanetRepository;
import com.stud.backend.dictionary.repository.SkillRepository;
import com.stud.backend.profiles.domain.ClientProfile;
import com.stud.backend.profiles.domain.HunterProfile;
import com.stud.backend.profiles.domain.HunterSkill;
import com.stud.backend.profiles.domain.HunterSkillId;
import com.stud.backend.profiles.domain.enums.AvailabilityStatus;
import com.stud.backend.profiles.repository.ClientProfileRepository;
import com.stud.backend.profiles.repository.HunterProfileRepository;
import com.stud.backend.profiles.repository.HunterSkillRepository;
import com.stud.backend.users.domain.Role;
import com.stud.backend.users.domain.User;
import com.stud.backend.users.domain.UserRole;
import com.stud.backend.users.domain.enums.RoleName;
import com.stud.backend.users.repository.RoleRepository;
import com.stud.backend.users.repository.UserRepository;
import com.stud.backend.users.repository.UserRoleRepository;
import com.stud.backend.profiles.web.dto.ProfileDtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    private final FactionRepository factionRepository;
    private final PlanetRepository planetRepository;
    private final SkillRepository skillRepository;

    private final ClientProfileRepository clientProfileRepository;
    private final HunterProfileRepository hunterProfileRepository;
    private final HunterSkillRepository hunterSkillRepository;

    public MyProfilesResponse getMyProfiles(String email) {
        User user = findUserByEmail(email);

        ClientProfileResponse clientProfile = clientProfileRepository.findByUserId(user.getId())
                .map(this::toClientProfileResponse)
                .orElse(null);

        HunterProfileResponse hunterProfile = hunterProfileRepository.findByUserId(user.getId())
                .map(this::toHunterProfileResponse)
                .orElse(null);

        return new MyProfilesResponse(clientProfile, hunterProfile);
    }

    public List<ClientProfileResponse> getClientProfiles() {
        return clientProfileRepository.findAll(Sort.by("name"))
                .stream()
                .map(this::toClientProfileResponse)
                .toList();
    }

    public List<HunterProfileResponse> getHunterProfiles() {
        List<HunterProfile> profiles = hunterProfileRepository.findAll(Sort.by("callsign"));
        Map<UUID, List<HunterSkillResponse>> skillsByHunterProfileId = getSkillsByHunterProfileId(profiles);

        return profiles
                .stream()
                .map(profile -> toHunterProfileResponse(
                        profile,
                        skillsByHunterProfileId.getOrDefault(profile.getId(), List.of())
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
        User user = findUserByEmail(email);

        if (clientProfileRepository.existsByUserId(user.getId())) {
            throw new DuplicateResourceException("Client profile already exists for current user");
        }

        ClientProfile profile = new ClientProfile();
        profile.setUser(user);
        profile.setName(request.name().trim());
        profile.setDescription(request.description());
        profile.setReliabilityScore(50);
        profile.setAverageRating(BigDecimal.ZERO);
        profile.setCompletedOrdersCount(0);
        profile.setCancelledOrdersCount(0);

        if (request.factionId() != null) {
            profile.setFaction(findFaction(request.factionId()));
        }

        if (request.planetId() != null) {
            profile.setPlanet(findPlanet(request.planetId()));
        }

        ClientProfile savedProfile = clientProfileRepository.save(profile);
        ensureUserRole(user, RoleName.CLIENT);

        return toClientProfileResponse(savedProfile);
    }

    @Transactional
    public HunterProfileResponse createMyHunterProfile(String email, HunterProfileCreateRequest request) {
        User user = findUserByEmail(email);
        String callsign = request.callsign().trim();

        if (hunterProfileRepository.existsByUserId(user.getId())) {
            throw new DuplicateResourceException("Hunter profile already exists for current user");
        }

        if (hunterProfileRepository.existsByCallsignIgnoreCase(callsign)) {
            throw new DuplicateResourceException("Hunter profile with callsign '%s' already exists".formatted(callsign));
        }

        HunterProfile profile = new HunterProfile();
        profile.setUser(user);
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
            profile.setFaction(findFaction(request.factionId()));
        }

        if (request.homePlanetId() != null) {
            profile.setHomePlanet(findPlanet(request.homePlanetId()));
        }

        HunterProfile savedProfile = hunterProfileRepository.save(profile);
        ensureUserRole(user, RoleName.HUNTER);

        return toHunterProfileResponse(savedProfile);
    }

    @Transactional
    public HunterProfileResponse addSkillToMyHunterProfile(String email, HunterSkillRequest request) {
        User user = findUserByEmail(email);

        HunterProfile hunterProfile = hunterProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Hunter profile not found for current user"));

        Skill skill = skillRepository.findById(request.skillId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + request.skillId()));

        HunterSkillId id = new HunterSkillId(hunterProfile.getId(), skill.getId());

        HunterSkill hunterSkill = hunterSkillRepository.findById(id)
                .orElseGet(() -> new HunterSkill(hunterProfile, skill, request.level()));

        hunterSkill.setLevel(request.level());
        hunterSkillRepository.save(hunterSkill);

        return toHunterProfileResponse(hunterProfile);
    }

    @Transactional
    public HunterProfileResponse removeSkillFromMyHunterProfile(String email, UUID skillId) {
        User user = findUserByEmail(email);

        HunterProfile hunterProfile = hunterProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Hunter profile not found for current user"));

        HunterSkillId id = new HunterSkillId(hunterProfile.getId(), skillId);
        hunterSkillRepository.deleteById(id);

        return toHunterProfileResponse(hunterProfile);
    }

    private void ensureUserRole(User user, RoleName roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        if (!userRoleRepository.existsByUserIdAndRoleId(user.getId(), role.getId())) {
            userRoleRepository.save(new UserRole(user, role));
        }
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private ClientProfile findClientProfile(UUID profileId) {
        return clientProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Client profile not found: " + profileId));
    }

    private HunterProfile findHunterProfile(UUID profileId) {
        return hunterProfileRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("Hunter profile not found: " + profileId));
    }

    private Faction findFaction(UUID factionId) {
        return factionRepository.findById(factionId)
                .orElseThrow(() -> new ResourceNotFoundException("Faction not found: " + factionId));
    }

    private Planet findPlanet(UUID planetId) {
        return planetRepository.findById(planetId)
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found: " + planetId));
    }

    private ClientProfileResponse toClientProfileResponse(ClientProfile profile) {
        Faction faction = profile.getFaction();
        Planet planet = profile.getPlanet();

        return new ClientProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getName(),
                profile.getDescription(),
                faction == null ? null : faction.getId(),
                faction == null ? null : faction.getName(),
                planet == null ? null : planet.getId(),
                planet == null ? null : planet.getName(),
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
        Faction faction = profile.getFaction();
        Planet homePlanet = profile.getHomePlanet();

        return new HunterProfileResponse(
                profile.getId(),
                profile.getUser().getId(),
                profile.getCallsign(),
                profile.getBio(),
                faction == null ? null : faction.getId(),
                faction == null ? null : faction.getName(),
                homePlanet == null ? null : homePlanet.getId(),
                homePlanet == null ? null : homePlanet.getName(),
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

        return hunterSkillRepository.findAllByHunterProfile_IdIn(hunterProfileIds)
                .stream()
                .collect(Collectors.groupingBy(
                        hunterSkill -> hunterSkill.getId().getHunterProfileId(),
                        Collectors.mapping(this::toHunterSkillResponse, Collectors.toList())
                ));
    }

    private HunterSkillResponse toHunterSkillResponse(HunterSkill hunterSkill) {
        return new HunterSkillResponse(
                hunterSkill.getSkill().getId(),
                hunterSkill.getSkill().getName(),
                hunterSkill.getLevel()
        );
    }
}
