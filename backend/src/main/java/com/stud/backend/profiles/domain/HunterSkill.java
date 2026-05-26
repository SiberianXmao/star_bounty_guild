package com.stud.backend.profiles.domain;


import com.stud.backend.dictionary.domain.Skill;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("skillId")
    @JoinColumn(name = "skill_id", nullable = false)
    private Skill skill;

    @Column(name = "level", nullable = false)
    private Integer level;

    public HunterSkill(HunterProfile hunterProfile, Skill skill, Integer level) {
        this.hunterProfile = hunterProfile;
        this.skill = skill;
        this.level = level;
        this.id = new HunterSkillId(hunterProfile.getId(), skill.getId());
    }
}
