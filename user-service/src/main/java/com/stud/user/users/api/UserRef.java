package com.stud.user.users.api;

import java.util.UUID;

public record UserRef(
        UUID id,
        String email,
        String username,
        String displayName,
        String avatarUrl
) {
}
