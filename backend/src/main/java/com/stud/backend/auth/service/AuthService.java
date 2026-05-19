package com.stud.backend.auth.service;


import com.stud.backend.common.exception.DuplicateResourceException;
import com.stud.backend.common.exception.ResourceNotFoundException;
import com.stud.backend.users.domain.Role;
import com.stud.backend.users.domain.User;
import com.stud.backend.users.domain.UserRole;
import com.stud.backend.users.domain.enums.RoleName;
import com.stud.backend.users.domain.enums.UserStatus;
import com.stud.backend.users.repository.RoleRepository;
import com.stud.backend.users.repository.UserRepository;
import com.stud.backend.users.repository.UserRoleRepository;
import com.stud.backend.auth.web.dto.AuthDtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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

        return toAuthResponse(savedUser, roles, accessToken);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new DisabledException("User is not active");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        Set<RoleName> roles = getRoles(user);
        String accessToken = jwtService.generateAccessToken(user, roles);

        return toAuthResponse(user, roles, accessToken);
    }

    public UserSummary me(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        return toUserSummary(user, getRoles(user));
    }

    private Set<RoleName> getRoles(User user) {
        return userRoleRepository.findAllByUserId(user.getId())
                .stream()
                .map(UserRole::getRole)
                .map(Role::getName)
                .collect(Collectors.toSet());
    }

    private AuthResponse toAuthResponse(User user, Set<RoleName> roles, String accessToken) {
        return new AuthResponse(
                "Bearer",
                accessToken,
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
