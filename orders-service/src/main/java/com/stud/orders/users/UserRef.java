package com.stud.orders.users;

import java.util.UUID;

public record UserRef(
        UUID id,
        String email,
        String username,
        String displayName
) {
}
