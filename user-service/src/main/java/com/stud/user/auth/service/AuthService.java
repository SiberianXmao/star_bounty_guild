package com.stud.user.auth.service;


import com.stud.user.common.exception.DuplicateResourceException;
import com.stud.user.common.exception.ResourceNotFoundException;
import com.stud.user.users.domain.Role;
import com.stud.user.users.domain.User;
import com.stud.user.users.domain.UserRole;
import com.stud.user.users.domain.enums.RoleName;
import com.stud.user.users.domain.enums.UserStatus;
import com.stud.user.users.repository.RoleRepository;
import com.stud.user.users.repository.UserRepository;
import com.stud.user.users.repository.UserRoleRepository;
import com.stud.user.auth.web.dto.AuthDtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        String username = request.username().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("User with email '%s' already exists".formatted(email));
        }

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateResourceException("User with username '%s' already exists".formatted(username));
        }

        Role clientRole = roleRepository.findByName(RoleName.CLIENT)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: CLIENT"));

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setDisplayName(request.displayName());
        user.setAvatarUrl(request.avatarUrl());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);
        userRoleRepository.save(new UserRole(savedUser, clientRole));

        Set<RoleName> roles = Set.of(RoleName.CLIENT);

        String accessToken = jwtService.generateAccessToken(savedUser, roles);
        String refreshToken = refreshTokenService.createRefreshToken(savedUser);

        return toAuthResponse(savedUser, roles, accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        validateActiveUser(user);

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        Set<RoleName> roles = getRoles(user);

        String accessToken = jwtService.generateAccessToken(user, roles);
        String refreshToken = refreshTokenService.createRefreshToken(user);

        return toAuthResponse(user, roles, accessToken, refreshToken);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        User user = refreshTokenService.validateAndRotate(request.refreshToken());

        validateActiveUser(user);

        Set<RoleName> roles = getRoles(user);

        String accessToken = jwtService.generateAccessToken(user, roles);
        String newRefreshToken = refreshTokenService.createRefreshToken(user);

        return toAuthResponse(user, roles, accessToken, newRefreshToken);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        refreshTokenService.revoke(request.refreshToken());
    }

    @Transactional
    public UserSummary me(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {
            return meFromKeycloak(jwtAuthentication);
        }

        return me(authentication.getName());
    }

    public UserSummary me(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        return toUserSummary(user, getRoles(user));
    }

    private UserSummary meFromKeycloak(JwtAuthenticationToken authentication) {
        Jwt jwt = authentication.getToken();
        String email = requiredClaim(jwt, "email").trim().toLowerCase();
        Set<RoleName> roles = extractRoleNames(authentication.getAuthorities());

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> createExternalUser(jwt, email));

        validateActiveUser(user);
        updateExternalUser(user, jwt);
        Set<RoleName> syncedRoles = syncExternalRoles(user, roles);

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return toUserSummary(user, syncedRoles);
    }

    private User createExternalUser(Jwt jwt, String email) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(resolveUniqueUsername(jwt, email));
        user.setDisplayName(resolveDisplayName(jwt, email));
        user.setPasswordHash("{external}keycloak");
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(Boolean.TRUE.equals(jwt.getClaimAsBoolean("email_verified")));

        return userRepository.save(user);
    }

    private void updateExternalUser(User user, Jwt jwt) {
        String displayName = resolveDisplayName(jwt, user.getEmail());

        if (user.getDisplayName() == null || user.getDisplayName().isBlank()) {
            user.setDisplayName(displayName);
        }

        user.setEmailVerified(Boolean.TRUE.equals(jwt.getClaimAsBoolean("email_verified")));
    }

    private Set<RoleName> syncExternalRoles(User user, Set<RoleName> desiredRoles) {
        Set<RoleName> rolesToApply = desiredRoles.isEmpty() ? Set.of(RoleName.CLIENT) : desiredRoles;
        Set<RoleName> currentRoles = getRoles(user);

        for (RoleName roleName : rolesToApply) {
            if (!currentRoles.contains(roleName)) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
                userRoleRepository.save(new UserRole(user, role));
            }
        }

        userRoleRepository.findAllByUserId(user.getId())
                .stream()
                .filter(userRole -> !rolesToApply.contains(userRole.getRole().getName()))
                .forEach(userRoleRepository::delete);

        return rolesToApply;
    }

    private Set<RoleName> extractRoleNames(Collection<? extends GrantedAuthority> authorities) {
        Set<RoleName> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(this::toRoleName)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());

        return roles.isEmpty() ? Set.of(RoleName.CLIENT) : roles;
    }

    private Optional<RoleName> toRoleName(String authority) {
        String roleName = authority.startsWith("ROLE_") ? authority.substring(5) : authority;

        try {
            return Optional.of(RoleName.valueOf(roleName));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    private String requiredClaim(Jwt jwt, String claimName) {
        String value = jwt.getClaimAsString(claimName);

        if (value == null || value.isBlank()) {
            throw new ResourceNotFoundException("Keycloak token has no required claim: " + claimName);
        }

        return value;
    }

    private String resolveDisplayName(Jwt jwt, String email) {
        String name = jwt.getClaimAsString("name");

        if (name != null && !name.isBlank()) {
            return name.trim();
        }

        String givenName = jwt.getClaimAsString("given_name");
        String familyName = jwt.getClaimAsString("family_name");
        String fullName = Stream.of(givenName, familyName)
                .filter(part -> part != null && !part.isBlank())
                .map(String::trim)
                .collect(Collectors.joining(" "));

        return fullName.isBlank() ? getEmailLocalPart(email) : fullName;
    }

    private String resolveUniqueUsername(Jwt jwt, String email) {
        String candidate = jwt.getClaimAsString("preferred_username");

        if (candidate == null || candidate.isBlank()) {
            candidate = getEmailLocalPart(email);
        }

        String username = normalizeUsername(candidate);

        if (!userRepository.existsByUsernameIgnoreCase(username)) {
            return username;
        }

        String subject = Optional.ofNullable(jwt.getSubject()).orElse(UUID.randomUUID().toString());
        String compactSubject = subject.replace("-", "");
        String suffix = "-" + compactSubject.substring(0, Math.min(compactSubject.length(), 8));
        int maxBaseLength = Math.max(1, 80 - suffix.length());
        String usernameWithSuffix = username.substring(0, Math.min(username.length(), maxBaseLength)) + suffix;

        if (!userRepository.existsByUsernameIgnoreCase(usernameWithSuffix)) {
            return usernameWithSuffix;
        }

        return UUID.randomUUID().toString();
    }

    private String normalizeUsername(String value) {
        String normalized = value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_.@-]", "-")
                .replaceAll("-{2,}", "-");

        if (normalized.isBlank()) {
            return UUID.randomUUID().toString();
        }

        return normalized.substring(0, Math.min(normalized.length(), 80));
    }

    private String getEmailLocalPart(String email) {
        int atIndex = email.indexOf('@');
        return atIndex > 0 ? email.substring(0, atIndex) : email;
    }

    private void validateActiveUser(User user) {
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new DisabledException("User is not active");
        }
    }

    private Set<RoleName> getRoles(User user) {
        return userRoleRepository.findAllByUserId(user.getId())
                .stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    private AuthResponse toAuthResponse(
            User user,
            Set<RoleName> roles,
            String accessToken,
            String refreshToken
    ) {
        return new AuthResponse(
                "Bearer",
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirationSeconds(),
                toUserSummary(user, roles)
        );
    }

    private UserSummary toUserSummary(User user, Set<RoleName> roles) {
        return new UserSummary(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                user.getAvatarUrl(),
                user.getStatus(),
                roles
        );
    }
}
