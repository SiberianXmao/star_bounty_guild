package com.stud.profiles.users;

import java.util.UUID;

public interface UserRoleManager {

    void ensureRole(UUID userId, UserRoleNameRef roleName);
}
