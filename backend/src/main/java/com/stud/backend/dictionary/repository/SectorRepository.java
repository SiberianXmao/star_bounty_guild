package com.stud.backend.dictionary.repository;

import com.stud.backend.dictionary.domain.Sector;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SectorRepository extends JpaRepository<Sector, UUID> {
    boolean existsByNameIgnoreCase(String name);
}