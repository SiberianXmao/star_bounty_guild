package com.stud.dictionary.repository;

import com.stud.dictionary.domain.Sector;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SectorRepository extends JpaRepository<Sector, UUID> {

    @Override
    @EntityGraph(attributePaths = "controllingFaction")
    List<Sector> findAll(Sort sort);

    boolean existsByNameIgnoreCase(String name);
}
