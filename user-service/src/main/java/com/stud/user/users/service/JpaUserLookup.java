package com.stud.user.users.service;

import com.stud.user.common.exception.ResourceNotFoundException;
import com.stud.user.users.api.UserLookup;
import com.stud.user.users.api.UserRef;
import com.stud.user.users.domain.User;
import com.stud.user.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private UserRef toRef(User user) {
        return new UserRef(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getDisplayName()
        );
    }
}