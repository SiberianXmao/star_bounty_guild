package com.stud.dictionary.repository;

import com.stud.dictionary.domain.Planet;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlanetRepository extends JpaRepository<Planet, UUID> {

    @Override
    @EntityGraph(attributePaths = {"sector", "controllingFaction"})
    List<Planet> findAll(Sort sort);

    boolean existsByNameIgnoreCase(String name);
}
