package com.stud.backend.dictionary.domain;


import com.stud.backend.common.persistence.BaseUuidEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sectors", schema = "bounty")
public class Sector extends BaseUuidEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "controlling_faction_id")
    private Faction controllingFaction;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "stability_level", nullable = false)
    private Integer stabilityLevel;

    @Column(name = "danger_level", nullable = false)
    private Integer dangerLevel;
}