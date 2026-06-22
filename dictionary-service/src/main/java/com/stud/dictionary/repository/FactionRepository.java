package com.stud.dictionary.repository;

import com.stud.dictionary.domain.Faction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FactionRepository extends JpaRepository<Faction, UUID> {
    boolean existsByNameIgnoreCase(String name);
}