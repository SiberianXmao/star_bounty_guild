package com.stud.user.media.service;

import com.stud.user.common.exception.ResourceNotFoundException;
import com.stud.user.users.domain.User;
import com.stud.user.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvatarAccountService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public AvatarOwner getOwner(String email) {
        User user = findByEmail(email);
        return new AvatarOwner(user.getId(), user.getAvatarUrl());
    }

    @Transactional
    public AvatarAssignment replace(UUID userId, String avatarUrl) {
        User user = findById(userId);
        String previousUrl = user.getAvatarUrl();
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);
        return new AvatarAssignment(avatarUrl, previousUrl);
    }

    @Transactional
    public AvatarAssignment clear(UUID userId) {
        User user = findById(userId);
        String previousUrl = user.getAvatarUrl();
        user.setAvatarUrl(null);
        userRepository.save(user);
        return new AvatarAssignment(null, previousUrl);
    }

    private User findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private User findById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    public record AvatarOwner(UUID userId, String avatarUrl) {
    }

    public record AvatarAssignment(String avatarUrl, String previousUrl) {
    }
}
