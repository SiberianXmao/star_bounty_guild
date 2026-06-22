package com.stud.user.users.web;

import com.stud.user.users.service.UserService;
import com.stud.user.users.web.dto.UserDtos.UserResponse;
import com.stud.user.users.web.dto.UserDtos.UserUpdateStatusRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/moderation")
@PreAuthorize("hasAnyAuthority('ADMIN', 'MODERATOR')")
public class ModerationController {

    private final UserService userService;

    @GetMapping("/users")
    public List<UserResponse> getUsers() {
        return userService.getUsers();
    }

    @PatchMapping("/users/{userId}/status")
    public UserResponse updateUserStatus(
            @PathVariable UUID userId,
            @Valid @RequestBody UserUpdateStatusRequest request
    ) {
        return userService.moderateUserStatus(userId, request);
    }
}
