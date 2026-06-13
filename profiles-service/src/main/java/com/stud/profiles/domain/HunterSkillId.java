package com.stud.profiles.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class HunterSkillId implements Serializable {

    @Column(name = "hunter_profile_id")
    private UUID hunterProfileId;

    @Column(name = "skill_id")
    private UUID skillId;

    public HunterSkillId(UUID hunterProfileId, UUID skillId) {
        this.hunterProfileId = hunterProfileId;
        this.skillId = skillId;
    }
}