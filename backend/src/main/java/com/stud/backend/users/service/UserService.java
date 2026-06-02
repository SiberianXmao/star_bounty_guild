package com.stud.backend.users.service;


import com.stud.backend.common.exception.BadRequestException;
import com.stud.backend.common.exception.DuplicateResourceException;
import com.stud.backend.common.exception.ResourceNotFoundException;
import com.stud.backend.users.domain.Role;
import com.stud.backend.users.domain.User;
import com.stud.backend.users.domain.UserRole;
import com.stud.backend.users.domain.UserRoleId;
import com.stud.backend.users.domain.enums.RoleName;
import com.stud.backend.users.domain.enums.UserStatus;
import com.stud.backend.users.repository.RoleRepository;
import com.stud.backend.users.repository.UserRepository;
import com.stud.backend.users.repository.UserRoleRepository;
import com.stud.backend.users.web.dto.UserDtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getUsers() {
        List<User> users = userRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        Map<UUID, Set<RoleName>> rolesByUserId = getRolesByUserId(users);

        return users
                .stream()
                .map(user -> toUserResponse(user, rolesByUserId.getOrDefault(user.getId(), Set.of())))
                .toList();
    }

    public UserResponse getUser(UUID userId) {
        return toUserResponse(findUser(userId));
    }

    public List<RoleResponse> getRoles() {
        return roleRepository.findAll(Sort.by("name"))
                .stream()
                .map(this::toRoleResponse)
                .toList();
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        String email = request.email().trim().toLowerCase();
        String username = request.username().trim();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("User with email '%s' already exists".formatted(email));
        }

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateResourceException("User with username '%s' already exists".formatted(username));
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setDisplayName(request.displayName());
        user.setAvatarUrl(request.avatarUrl());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStatus(UserStatus.ACTIVE);
        user.setEmailVerified(false);

        return toUserResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateUserStatus(UUID userId, UserUpdateStatusRequest request) {
        User user = findUser(userId);
        user.setStatus(request.status());

        return toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUserPermanently(UUID userId, String currentAdminEmail) {
        User user = findUser(userId);

        if (currentAdminEmail != null && user.getEmail().equalsIgnoreCase(currentAdminEmail)) {
            throw new BadRequestException("Admins cannot permanently delete their own account");
        }

        if (user.getStatus() != UserStatus.DELETED) {
            throw new BadRequestException("Only deleted users can be permanently removed");
        }

        if (userRepository.hasPermanentDeleteBlockers(user.getId())) {
            throw new BadRequestException("User has profile or activity history; keep archived instead");
        }

        userRepository.delete(user);
    }

    @Transactional
    public UserResponse moderateUserStatus(UUID userId, UserUpdateStatusRequest request) {
        if (request.status() != UserStatus.ACTIVE && request.status() != UserStatus.BLOCKED) {
            throw new BadRequestException("Moderators can only activate or block users");
        }

        User user = findUser(userId);
        Set<RoleName> roles = getRoles(user.getId());

        if (roles.contains(RoleName.ADMIN) || roles.contains(RoleName.MODERATOR)) {
            throw new BadRequestException("Moderators cannot change staff users");
        }

        user.setStatus(request.status());

        return toUserResponse(userRepository.save(user), roles);
    }

    @Transactional
    public UserResponse assignRole(UUID userId, AssignRoleRequest request) {
        User user = findUser(userId);
        Role role = findRole(request.roleName());

        if (!userRoleRepository.existsByUserIdAndRoleId(user.getId(), role.getId())) {
            UserRole userRole = new UserRole(user, role);
            userRoleRepository.save(userRole);
        }

        return toUserResponse(user);
    }

    @Transactional
    public UserResponse removeRole(UUID userId, RoleName roleName) {
        User user = findUser(userId);
        Role role = findRole(roleName);

        UserRoleId id = new UserRoleId(user.getId(), role.getId());
        userRoleRepository.deleteById(id);

        return toUserResponse(user);
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    private Role findRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));
    }

    private UserResponse toUserResponse(User user) {
        return toUserResponse(user, getRoles(user.getId()));
    }

    private UserResponse toUserResponse(User user, Set<RoleName> roles) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                user.getAvatarUrl(),
                user.getStatus(),
                user.getEmailVerified(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getLastLoginAt(),
                roles
        );
    }

    private Set<RoleName> getRoles(UUID userId) {
        return userRoleRepository.findAllByUserId(userId)
                .stream()
                .map(userRole -> userRole.getRole().getName())
                .collect(Collectors.toSet());
    }

    private Map<UUID, Set<RoleName>> getRolesByUserId(List<User> users) {
        if (users.isEmpty()) {
            return Map.of();
        }

        List<UUID> userIds = users.stream()
                .map(User::getId)
                .toList();

        return userRoleRepository.findAllByUser_IdIn(userIds)
                .stream()
                .collect(Collectors.groupingBy(
                        userRole -> userRole.getId().getUserId(),
                        Collectors.mapping(userRole -> userRole.getRole().getName(), Collectors.toSet())
                ));
    }

    private RoleResponse toRoleResponse(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription()
        );
    }
}
