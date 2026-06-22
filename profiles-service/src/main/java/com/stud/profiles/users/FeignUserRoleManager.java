package com.stud.profiles.users;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeignUserRoleManager implements UserRoleManager {

    private final UserServiceFeignClient client;

    @Override
    public void ensureRole(UUID userId, UserRoleNameRef roleName) {
        client.ensureRole(userId, new UserServiceFeignClient.EnsureRoleRequest(roleName));
    }
}
