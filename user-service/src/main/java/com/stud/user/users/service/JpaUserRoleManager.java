package com.stud.user.users.service;

import com.stud.user.common.exception.ResourceNotFoundException;
import com.stud.user.users.api.UserRoleManager;
import com.stud.user.users.api.UserRoleNameRef;
import com.stud.user.users.domain.Role;
import com.stud.user.users.domain.User;
import com.stud.user.users.domain.UserRole;
import com.stud.user.users.domain.enums.RoleName;
import com.stud.user.users.repository.RoleRepository;
import com.stud.user.users.repository.UserRepository;
import com.stud.user.users.repository.UserRoleRepository;
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
