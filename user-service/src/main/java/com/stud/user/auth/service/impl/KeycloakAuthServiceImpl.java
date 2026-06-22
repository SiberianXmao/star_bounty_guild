package com.stud.user.auth.service.impl;

import com.stud.user.auth.keycloak.KeycloakIdentityClient;
import com.stud.user.auth.keycloak.KeycloakIdentityClient.TokenResponse;
import com.stud.user.auth.service.AuthService;
import com.stud.user.auth.web.dto.AuthDtos.AuthResponse;
import com.stud.user.auth.web.dto.AuthDtos.LoginRequest;
import com.stud.user.auth.web.dto.AuthDtos.LogoutRequest;
import com.stud.user.auth.web.dto.AuthDtos.RefreshTokenRequest;
import com.stud.user.auth.web.dto.AuthDtos.RegisterRequest;
import com.stud.user.auth.web.dto.AuthDtos.UserSummary;
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
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@ConditionalOnProperty(prefix = "app.security", name = "provider", havingValue = "keycloak")
public class KeycloakAuthServiceImpl implements AuthService {

    private static final String EXTERNAL_PASSWORD_MARKER = "{external}keycloak";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final KeycloakIdentityClient keycloakIdentityClient;
    private final JwtDecoder jwtDecoder;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        String username = request.username().trim();
        Optional<User> existingUser = userRepository.findByEmailIgnoreCase(email);

        userRepository.findByUsernameIgnoreCase(username)
                .filter(user -> existingUser.isEmpty() || !user.getId().equals(existingUser.get().getId()))
                .ifPresent(user -> {
                    throw new DuplicateResourceException(
                            "User with username '%s' already exists".formatted(username)
                    );
                });

        String keycloakUserId = keycloakIdentityClient.createUser(request);

        try {
            User user = existingUser.orElseGet(User::new);
            user.setEmail(email);
            user.setUsername(username);
            user.setDisplayName(request.displayName());
            user.setAvatarUrl(null);
            user.setPasswordHash(EXTERNAL_PASSWORD_MARKER);
            user.setStatus(UserStatus.ACTIVE);
            user.setEmailVerified(false);
            userRepository.save(user);

            TokenResponse tokens = keycloakIdentityClient.login(email, request.password());
            User synchronizedUser = synchronizeUser(jwtDecoder.decode(tokens.accessToken()));

            return toAuthResponse(synchronizedUser, tokens);
        } catch (RuntimeException exception) {
            keycloakIdentityClient.deleteUser(keycloakUserId);
            throw exception;
        }
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        TokenResponse tokens;

        try {
            tokens = keycloakIdentityClient.login(email, request.password());
        } catch (BadCredentialsException exception) {
            tokens = migrateLocalUser(email, request.password(), exception);
        }

        User user = synchronizeUser(jwtDecoder.decode(tokens.accessToken()));
        validateActiveUser(user);

        return toAuthResponse(user, tokens);
    }

    private TokenResponse migrateLocalUser(
            String email,
            String password,
            BadCredentialsException originalException
    ) {
        User user = userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> originalException);
        validateActiveUser(user);

        if (EXTERNAL_PASSWORD_MARKER.equals(user.getPasswordHash())
                || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw originalException;
        }

        RegisterRequest migrationRequest = new RegisterRequest(
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                password
        );
        String keycloakUserId = keycloakIdentityClient.createUser(migrationRequest);

        try {
            TokenResponse tokens = keycloakIdentityClient.login(email, password);
            user.setPasswordHash(EXTERNAL_PASSWORD_MARKER);
            userRepository.save(user);
            return tokens;
        } catch (RuntimeException exception) {
            keycloakIdentityClient.deleteUser(keycloakUserId);
            throw exception;
        }
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        TokenResponse tokens = keycloakIdentityClient.refresh(request.refreshToken());
        User user = synchronizeUser(jwtDecoder.decode(tokens.accessToken()));
        validateActiveUser(user);

        return toAuthResponse(user, tokens);
    }

    @Override
    public void logout(LogoutRequest request) {
        keycloakIdentityClient.logout(request.refreshToken());
    }

    @Override
    @Transactional
    public UserSummary me(Authentication authentication) {
        if (!(authentication instanceof JwtAuthenticationToken jwtAuthentication)) {
            throw new BadCredentialsException("Keycloak authentication is required");
        }

        User user = synchronizeUser(jwtAuthentication.getToken());
        validateActiveUser(user);

        return toUserSummary(user, getRoles(user));
    }

    @Override
    public UserSummary me(String email) {
        User user = userRepository.findByEmailIgnoreCase(normalizeEmail(email))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        return toUserSummary(user, getRoles(user));
    }

    private User synchronizeUser(Jwt jwt) {
        String email = requiredClaim(jwt, "email").trim().toLowerCase(Locale.ROOT);
        Set<RoleName> roles = extractRoleNames(jwt);
        User user = userRepository.findByEmailIgnoreCase(email).orElseGet(User::new);

        if (user.getId() == null) {
            user.setEmail(email);
            user.setUsername(resolveUniqueUsername(jwt, email));
            user.setStatus(UserStatus.ACTIVE);
            user.setAvatarUrl(jwt.getClaimAsString("picture"));
        }

        if (user.getDisplayName() == null || user.getDisplayName().isBlank()) {
            user.setDisplayName(resolveDisplayName(jwt, email));
        }
        user.setPasswordHash(EXTERNAL_PASSWORD_MARKER);
        user.setEmailVerified(Boolean.TRUE.equals(jwt.getClaimAsBoolean("email_verified")));
        user.setLastLoginAt(Instant.now());
        User savedUser = userRepository.save(user);

        syncRoles(savedUser, roles);
        return savedUser;
    }

    private void syncRoles(User user, Set<RoleName> desiredRoles) {
        Set<RoleName> rolesToApply = desiredRoles.isEmpty() ? Set.of(RoleName.CLIENT) : desiredRoles;
        Set<RoleName> currentRoles = getRoles(user);

        for (RoleName roleName : rolesToApply) {
            if (!currentRoles.contains(roleName)) {
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
                userRoleRepository.save(new UserRole(user, role));
            }
        }

        userRoleRepository.findAllByUserId(user.getId()).stream()
                .filter(userRole -> !rolesToApply.contains(userRole.getRole().getName()))
                .forEach(userRoleRepository::delete);
    }

    private Set<RoleName> extractRoleNames(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        Object roles = realmAccess == null ? null : realmAccess.get("roles");

        if (!(roles instanceof Collection<?> roleCollection)) {
            return Set.of(RoleName.CLIENT);
        }

        Set<RoleName> roleNames = roleCollection.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(this::toRoleName)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());

        return roleNames.isEmpty() ? Set.of(RoleName.CLIENT) : roleNames;
    }

    private Optional<RoleName> toRoleName(String roleName) {
        try {
            return Optional.of(RoleName.valueOf(roleName));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
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
        String username = normalizeUsername(candidate == null ? getEmailLocalPart(email) : candidate);

        if (!userRepository.existsByUsernameIgnoreCase(username)) {
            return username;
        }

        String subject = Optional.ofNullable(jwt.getSubject()).orElse(UUID.randomUUID().toString());
        String compactSubject = subject.replace("-", "");
        String suffix = "-" + compactSubject.substring(0, Math.min(compactSubject.length(), 8));
        int maxBaseLength = Math.max(1, 80 - suffix.length());
        return username.substring(0, Math.min(username.length(), maxBaseLength)) + suffix;
    }

    private String normalizeUsername(String value) {
        String normalized = value.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9_.@-]", "-")
                .replaceAll("-{2,}", "-");

        return normalized.isBlank() ? UUID.randomUUID().toString() : normalized.substring(0, Math.min(80, normalized.length()));
    }

    private String requiredClaim(Jwt jwt, String claimName) {
        String value = jwt.getClaimAsString(claimName);

        if (value == null || value.isBlank()) {
            throw new BadCredentialsException("Keycloak token has no required claim: " + claimName);
        }

        return value;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
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
        return userRoleRepository.findAllByUserId(user.getId()).stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    private AuthResponse toAuthResponse(User user, TokenResponse tokens) {
        return new AuthResponse(
                tokens.tokenType() == null ? "Bearer" : tokens.tokenType(),
                tokens.accessToken(),
                tokens.refreshToken(),
                tokens.expiresInSeconds(),
                toUserSummary(user, getRoles(user))
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
