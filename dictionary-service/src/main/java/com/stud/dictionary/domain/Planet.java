package com.stud.dictionary.domain;


import com.stud.dictionary.common.persistence.BaseUuidEntity;
import com.stud.dictionary.domain.enums.PlanetStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "planets", schema = "bounty")
public class Planet extends BaseUuidEntity {

    // подумать над связями один ко многим и тд
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id", nullable = false)
    private Sector sector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "controlling_faction_id")
    private Faction controllingFaction;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "danger_level", nullable = false)
    private Integer dangerLevel;

    @Column(name = "development_level", nullable = false)
    private Integer developmentLevel;

    @Column(name = "climate", length = 120)
    private String climate;

    @Column(name = "population")
    private Long population;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "status", nullable = false, columnDefinition = "planet_status")
    private PlanetStatus status;
}