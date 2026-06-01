package com.stud.backend.profiles.repository;

import com.stud.backend.profiles.domain.HunterSkill;
import com.stud.backend.profiles.domain.HunterSkillId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface HunterSkillRepository extends JpaRepository<HunterSkill, HunterSkillId> {

    @EntityGraph(attributePaths = "skill")
    List<HunterSkill> findAllByHunterProfileId(UUID hunterProfileId);

    @EntityGraph(attributePaths = "skill")
    List<HunterSkill> findAllByHunterProfile_IdIn(Collection<UUID> hunterProfileIds);
}
