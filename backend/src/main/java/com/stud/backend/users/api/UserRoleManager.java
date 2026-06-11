package com.stud.backend.users.api;

import java.util.UUID;

public interface UserRoleManager {

    void ensureRole(UUID userId, UserRoleNameRef roleName);
}
