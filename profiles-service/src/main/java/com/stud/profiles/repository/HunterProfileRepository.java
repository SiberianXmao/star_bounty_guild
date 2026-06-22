package com.stud.profiles.repository;


import com.stud.profiles.domain.HunterProfile;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HunterProfileRepository extends JpaRepository<HunterProfile, UUID> {

    @Override
    List<HunterProfile> findAll(Sort sort);

    @Override
    Optional<HunterProfile> findById(UUID id);

    Optional<HunterProfile> findByUserId(UUID userId);

    List<HunterProfile> findByIdIn(Collection<UUID> ids);

    boolean existsByUserId(UUID userId);

    boolean existsByCallsignIgnoreCase(String callsign);
}
