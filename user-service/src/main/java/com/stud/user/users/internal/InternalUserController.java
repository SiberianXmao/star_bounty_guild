package com.stud.user.users.internal;

import com.stud.user.users.api.UserLookup;
import com.stud.user.users.api.UserRef;
import com.stud.user.users.api.UserRoleManager;
import com.stud.user.users.api.UserRoleNameRef;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/users")
public class InternalUserController {

    private final UserLookup userLookup;
    private final UserRoleManager userRoleManager;

    @GetMapping("/by-email")
    public UserRef getByEmail(@RequestParam String email) {
        return userLookup.getByEmail(email);
    }

    @GetMapping("/{userId}")
    public UserRef getById(@PathVariable UUID userId) {
        return userLookup.getById(userId);
    }

    @PostMapping("/lookup")
    public List<UserRef> getByIds(@RequestBody UserIdsRequest request) {
        return userLookup.getByIds(request.userIds());
    }

    @PostMapping("/{userId}/roles")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ensureRole(
            @PathVariable UUID userId,
            @RequestBody EnsureRoleRequest request
    ) {
        userRoleManager.ensureRole(userId, request.roleName());
    }

    public record EnsureRoleRequest(UserRoleNameRef roleName) {
    }

    public record UserIdsRequest(List<UUID> userIds) {
    }
}
