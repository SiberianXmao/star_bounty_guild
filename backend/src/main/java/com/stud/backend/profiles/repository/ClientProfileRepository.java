package com.stud.backend.profiles.repository;


import com.stud.backend.profiles.domain.ClientProfile;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientProfileRepository extends JpaRepository<ClientProfile, UUID> {

    @Override
    @EntityGraph(attributePaths = "user")
    List<ClientProfile> findAll(Sort sort);

    @Override
    @EntityGraph(attributePaths = "user")
    Optional<ClientProfile> findById(UUID id);

    @EntityGraph(attributePaths = "user")
    Optional<ClientProfile> findByUserId(UUID userId);

    @EntityGraph(attributePaths = "user")
    List<ClientProfile> findByIdIn(Collection<UUID> ids);

    boolean existsByUserId(UUID userId);
}
