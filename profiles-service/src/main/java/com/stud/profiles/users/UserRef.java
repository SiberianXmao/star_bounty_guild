package com.stud.profiles.users;

import java.util.UUID;

public record UserRef(
        UUID id,
        String email,
        String username,
        String displayName
) {
}
