package com.stud.user.users.service;

import com.stud.user.common.exception.ResourceNotFoundException;
import com.stud.user.users.api.UserLookup;
import com.stud.user.users.api.UserRef;
import com.stud.user.users.domain.User;
import com.stud.user.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaUserLookup implements UserLookup {

    private final UserRepository userRepository;

    @Override
    public UserRef getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(this::toRef)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    @Override
    public UserRef getById(UUID userId) {
        return userRepository.findById(userId)
                .map(this::toRef)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
    }

    @Override
    public List<UserRef> getByIds(Collection<UUID> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        List<UUID> distinctIds = userIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        return userRepository.findAllById(distinctIds)
                .stream()
                .map(this::toRef)
                .toList();
    }

    private UserRef toRef(User user) {
        return new UserRef(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName(),
                user.getAvatarUrl()
        );
    }
}
