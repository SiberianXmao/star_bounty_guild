package com.stud.backend.dictionary.api;

import java.util.UUID;

public interface DictionaryLookup {

    FactionRef getFaction(UUID factionId);

    SectorRef getSector(UUID sectorId);

    PlanetRef getPlanet(UUID planetId);

    CurrencyRef getCurrency(String currencyCode);

    OrderCategoryRef getOrderCategory(UUID categoryId);

    SkillRef getSkill(UUID skillId);
}
