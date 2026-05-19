package com.stud.backend.dictionary.repository;

import com.stud.backend.dictionary.domain.Planet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlanetRepository extends JpaRepository<Planet, UUID> {
    boolean existsByNameIgnoreCase(String name);
}