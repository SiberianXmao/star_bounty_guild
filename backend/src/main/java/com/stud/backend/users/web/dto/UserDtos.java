package com.stud.backend.users.web.dto;

import com.stud.backend.users.domain.enums.RoleName;
import com.stud.backend.users.domain.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public final class UserDtos {

    private UserDtos() {
    }

    public record UserCreateRequest(
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 3, max = 80) String username,
            @Size(max = 120) String displayName,
            String avatarUrl,

            // Временно. В auth-модуле заменим на normal password + BCrypt.
            @NotBlank String passwordHash
    ) {
    }

    public record UserUpdateStatusRequest(
            @NotNull UserStatus status
    ) {
    }

    public record AssignRoleRequest(
            @NotNull RoleName roleName
    ) {
    }

    public record RoleResponse(
            UUID id,
            RoleName name,
            String description
    ) {
    }

    public record UserResponse(
            UUID id,
            String email,
            String username,
            String displayName,
            String avatarUrl,
            UserStatus status,
            Boolean emailVerified,
            Instant createdAt,
            Instant updatedAt,
            Instant lastLoginAt,
            Set<RoleName> roles
    ) {
    }
}
