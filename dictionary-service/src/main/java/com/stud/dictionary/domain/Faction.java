package com.stud.dictionary.domain;


import com.stud.dictionary.common.persistence.BaseUuidEntity;
import com.stud.dictionary.domain.enums.FactionRelation;
import com.stud.dictionary.domain.enums.FactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "factions", schema = "bounty")
public class Faction extends BaseUuidEntity {

    @Column(name = "name", nullable = false, unique = true, length = 120)
    private String name;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "image_url", length = 1024)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "type", nullable = false, columnDefinition = "faction_type")
    private FactionType type;

    @Column(name = "influence_level", nullable = false)
    private Integer influenceLevel;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(name = "relation_to_guild", nullable = false, columnDefinition = "faction_relation")
    private FactionRelation relationToGuild;
}
