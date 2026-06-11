package com.stud.backend.profiles.repository;


import com.stud.backend.profiles.domain.ClientProfile;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientProfileRepository extends JpaRepository<ClientProfile, UUID> {

    @Override
    List<ClientProfile> findAll(Sort sort);

    @Override
    Optional<ClientProfile> findById(UUID id);

    Optional<ClientProfile> findByUserId(UUID userId);

    List<ClientProfile> findByIdIn(Collection<UUID> ids);

    boolean existsByUserId(UUID userId);
}
