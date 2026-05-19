package com.stud.backend.users.repository;

import com.stud.backend.users.domain.Role;
import com.stud.backend.users.domain.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RoleName name);

    boolean existsByName(RoleName name);
}