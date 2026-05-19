package com.stud.backend.users.repository;

import com.stud.backend.users.domain.UserRole;
import com.stud.backend.users.domain.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    List<UserRole> findAllByUserId(UUID userId);

    boolean existsByUserIdAndRoleId(UUID userId, UUID roleId);
}
