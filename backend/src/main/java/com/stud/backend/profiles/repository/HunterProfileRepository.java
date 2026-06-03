package com.stud.backend.profiles.repository;


import com.stud.backend.profiles.domain.HunterProfile;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HunterProfileRepository extends JpaRepository<HunterProfile, UUID> {

    @Override
    @EntityGraph(attributePaths = "user")
    List<HunterProfile> findAll(Sort sort);

    @Override
    @EntityGraph(attributePaths = "user")
    Optional<HunterProfile> findById(UUID id);

    @EntityGraph(attributePaths = "user")
    Optional<HunterProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    boolean existsByCallsignIgnoreCase(String callsign);
}
