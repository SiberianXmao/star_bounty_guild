package com.stud.backend.profiles.repository;


import com.stud.backend.profiles.domain.ClientProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientProfileRepository extends JpaRepository<ClientProfile, UUID> {

    Optional<ClientProfile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}