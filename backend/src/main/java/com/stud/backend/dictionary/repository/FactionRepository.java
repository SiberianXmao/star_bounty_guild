package com.stud.backend.dictionary.repository;

import com.stud.backend.dictionary.domain.Faction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FactionRepository extends JpaRepository<Faction, UUID> {
    boolean existsByNameIgnoreCase(String name);
}