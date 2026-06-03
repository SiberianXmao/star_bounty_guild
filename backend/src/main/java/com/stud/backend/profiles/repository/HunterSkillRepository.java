package com.stud.backend.profiles.repository;

import com.stud.backend.profiles.domain.HunterSkill;
import com.stud.backend.profiles.domain.HunterSkillId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface HunterSkillRepository extends JpaRepository<HunterSkill, HunterSkillId> {

    List<HunterSkill> findAllByHunterProfileId(UUID hunterProfileId);

    List<HunterSkill> findAllByHunterProfile_IdIn(Collection<UUID> hunterProfileIds);
}
