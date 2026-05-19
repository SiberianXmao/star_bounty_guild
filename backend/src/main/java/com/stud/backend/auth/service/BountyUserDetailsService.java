package com.stud.backend.auth.service;


import com.stud.backend.users.domain.User;
import com.stud.backend.users.domain.UserRole;
import com.stud.backend.users.domain.enums.UserStatus;
import com.stud.backend.users.repository.UserRepository;
import com.stud.backend.users.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BountyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new DisabledException("User is not active");
        }

        String[] authorities = userRoleRepository.findAllByUserId(user.getId())
                .stream()
                .map(UserRole::getRole)
                .map(role -> role.getName().name())
                .toArray(String[]::new);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .build();
    }
}