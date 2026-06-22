package com.stud.profiles.users;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "user-service",
        url = "${app.services.users.base-url}",
        path = "/internal/v1/users"
)
public interface UserServiceFeignClient {

    @GetMapping("/by-email")
    UserRef getByEmail(@RequestParam String email);

    @GetMapping("/{userId}")
    UserRef getById(@PathVariable UUID userId);

    @PostMapping("/lookup")
    List<UserRef> getByIds(@RequestBody UserIdsRequest request);

    @PostMapping("/{userId}/roles")
    void ensureRole(@PathVariable UUID userId, @RequestBody EnsureRoleRequest request);

    record EnsureRoleRequest(UserRoleNameRef roleName) {
    }

    record UserIdsRequest(List<UUID> userIds) {
    }
}
