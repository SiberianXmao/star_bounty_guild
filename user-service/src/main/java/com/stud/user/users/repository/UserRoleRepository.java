package com.stud.user.users.repository;

import com.stud.user.users.domain.UserRole;
import com.stud.user.users.domain.UserRoleId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

    @EntityGraph(attributePaths = "role")
    List<UserRole> findAllByUserId(UUID userId);

    @EntityGraph(attributePaths = "role")
    List<UserRole> findAllByUser_IdIn(Collection<UUID> userIds);

    boolean existsByUserIdAndRoleId(UUID userId, UUID roleId);
}
