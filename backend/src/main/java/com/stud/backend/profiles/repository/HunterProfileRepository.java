package com.stud.backend.profiles.repository;


import com.stud.backend.profiles.domain.HunterProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface HunterProfileRepository extends JpaRepository<HunterProfile, UUID> {

    Optional<HunterProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    boolean existsByCallsignIgnoreCase(String callsign);
}