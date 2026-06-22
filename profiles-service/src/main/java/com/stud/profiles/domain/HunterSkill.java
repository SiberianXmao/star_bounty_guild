package com.stud.profiles.domain;


import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "hunter_skills", schema = "bounty")
public class HunterSkill {

    @EmbeddedId
    private HunterSkillId id;

    // подумать над связями один ко многим и тд
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("hunterProfileId")
    @JoinColumn(name = "hunter_profile_id", nullable = false)
    private HunterProfile hunterProfile;

    @Column(name = "level", nullable = false)
    private Integer level;

    public HunterSkill(HunterProfile hunterProfile, UUID skillId, Integer level) {
        this.hunterProfile = hunterProfile;
        this.level = level;
        this.id = new HunterSkillId(hunterProfile.getId(), skillId);
    }
}
