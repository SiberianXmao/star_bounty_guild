package com.stud.user.users.web;


import com.stud.user.users.domain.enums.RoleName;
import com.stud.user.users.service.UserService;
import com.stud.user.users.web.dto.UserDtos.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@PreAuthorize("hasAuthority('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/users/{userId}")
    public UserResponse getUser(@PathVariable UUID userId) {
        return userService.getUser(userId);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @PatchMapping("/users/{userId}/status")
    public UserResponse updateUserStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateStatusRequest request
    ) {
        return userService.updateUserStatus(userId, request);
    }

    @PostMapping("/users/{userId}/roles")
    public UserResponse assignRole(
            @PathVariable UUID userId,
            @Valid @RequestBody AssignRoleRequest request
    ) {
        return userService.assignRole(userId, request);
    }

    @DeleteMapping("/users/{userId}/roles/{roleName}")
    public UserResponse removeRole(
            @PathVariable UUID userId,
            @PathVariable RoleName roleName
    ) {
        return userService.removeRole(userId, roleName);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserPermanently(
            @PathVariable UUID userId,
            Authentication authentication
    ) {
        userService.deleteUserPermanently(userId, authentication == null ? null : authentication.getName());
    }

    @GetMapping("/roles")
    public List<RoleResponse> getRoles() {
        return userService.getRoles();
    }
}
