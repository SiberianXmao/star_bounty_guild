package com.stud.backend.auth.web.dto;


import com.stud.backend.users.domain.enums.RoleName;
import com.stud.backend.users.domain.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

// переписать дто отдельно
public final class AuthDtos {

    private AuthDtos() {
    }

    public record RegisterRequest(
            @NotBlank @Email @Size(max = 255) String email,
            @NotBlank @Size(min = 3, max = 80) String username,
            @Size(max = 120) String displayName,
            String avatarUrl,
            @NotBlank @Size(min = 8, max = 72) String password
    ) {
    }

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {
    }

    public record RefreshTokenRequest(
            @NotBlank String refreshToken
    ) {
    }

    public record LogoutRequest(
            @NotBlank String refreshToken
    ) {
    }

    public record AuthResponse(
            String tokenType,
            String accessToken,
            String refreshToken,
            long expiresInSeconds,
            UserSummary user
    ) {
    }

    public record UserSummary(
            UUID id,
            String email,
            String username,
            String displayName,
            String avatarUrl,
            UserStatus status,
            Set<RoleName> roles
    ) {
    }
}