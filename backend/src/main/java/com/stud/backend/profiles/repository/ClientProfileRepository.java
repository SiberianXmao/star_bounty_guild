package com.stud.backend.profiles.repository;


import com.stud.backend.profiles.domain.ClientProfile;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientProfileRepository extends JpaRepository<ClientProfile, UUID> {

    @Override
    @EntityGraph(attributePaths = {"user", "faction", "planet"})
    List<ClientProfile> findAll(Sort sort);

    @Override
    @EntityGraph(attributePaths = {"user", "faction", "planet"})
    Optional<ClientProfile> findById(UUID id);

    @EntityGraph(attributePaths = {"user", "faction", "planet"})
    Optional<ClientProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
