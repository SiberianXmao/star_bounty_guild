package com.stud.backend.dictionary.repository;

import com.stud.backend.dictionary.domain.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID> {
    boolean existsByNameIgnoreCase(String name);
}
