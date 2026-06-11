package com.stud.backend.users.service;

import com.stud.backend.common.exception.ResourceNotFoundException;
import com.stud.backend.users.api.UserRoleManager;
import com.stud.backend.users.api.UserRoleNameRef;
import com.stud.backend.users.domain.Role;
import com.stud.backend.users.domain.User;
import com.stud.backend.users.domain.UserRole;
import com.stud.backend.users.domain.enums.RoleName;
import com.stud.backend.users.repository.RoleRepository;
import com.stud.backend.users.repository.UserRepository;
import com.stud.backend.users.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class JpaUserRoleManager implements UserRoleManager {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public void ensureRole(UUID userId, UserRoleNameRef roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Role role = roleRepository.findByName(RoleName.valueOf(roleName.name()))
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        if (!userRoleRepository.existsByUserIdAndRoleId(user.getId(), role.getId())) {
            userRoleRepository.save(new UserRole(user, role));
        }
    }
}
