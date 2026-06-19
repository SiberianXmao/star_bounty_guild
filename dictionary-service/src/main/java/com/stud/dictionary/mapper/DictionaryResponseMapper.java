package com.stud.dictionary.mapper;

import com.stud.dictionary.domain.Currency;
import com.stud.dictionary.domain.Faction;
import com.stud.dictionary.domain.OrderCategory;
import com.stud.dictionary.domain.Planet;
import com.stud.dictionary.domain.Sector;
import com.stud.dictionary.domain.Skill;
import com.stud.dictionary.web.dto.DictionaryDtos.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface DictionaryResponseMapper {

    FactionResponse toFactionResponse(Faction faction);

    @Mapping(
            target = "controllingFactionId",
            source = "controllingFaction.id"
    )
    SectorResponse toSectorResponse(Sector sector);

    @Mapping(
            target = "sectorId",
            source = "sector.id"
    )
    @Mapping(
            target = "controllingFactionId",
            source = "controllingFaction.id"
    )
    PlanetResponse toPlanetResponse(Planet planet);

    OrderCategoryResponse toOrderCategoryResponse(
            OrderCategory category
    );

    CurrencyResponse toCurrencyResponse(
            Currency currency
    );

    SkillResponse toSkillResponse(
            Skill skill
    );
}
