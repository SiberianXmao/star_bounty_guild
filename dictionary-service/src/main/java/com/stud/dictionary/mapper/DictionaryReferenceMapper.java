package com.stud.dictionary.mapper;

import com.stud.dictionary.domain.Currency;
import com.stud.dictionary.domain.Faction;
import com.stud.dictionary.domain.OrderCategory;
import com.stud.dictionary.domain.Planet;
import com.stud.dictionary.domain.Sector;
import com.stud.dictionary.domain.Skill;
import com.stud.dictionary.internal.dto.DictionaryLookupDtos.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface DictionaryReferenceMapper {

    FactionRef toFactionRef(Faction faction);

    @Mapping(
            target = "controllingFactionId",
            source = "controllingFaction.id"
    )
    SectorRef toSectorRef(Sector sector);

    @Mapping(
            target = "sectorId",
            source = "sector.id"
    )
    @Mapping(
            target = "controllingFactionId",
            source = "controllingFaction.id"
    )
    PlanetRef toPlanetRef(Planet planet);

    CurrencyRef toCurrencyRef(Currency currency);

    OrderCategoryRef toOrderCategoryRef(
            OrderCategory category
    );

    SkillRef toSkillRef(Skill skill);
}