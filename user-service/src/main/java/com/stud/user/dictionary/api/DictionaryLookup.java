package com.stud.user.dictionary.api;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface DictionaryLookup {

    FactionRef getFaction(UUID factionId);

    SectorRef getSector(UUID sectorId);

    PlanetRef getPlanet(UUID planetId);

    CurrencyRef getCurrency(String currencyCode);

    OrderCategoryRef getOrderCategory(UUID categoryId);

    SkillRef getSkill(UUID skillId);

    Map<UUID,FactionRef> getFactionsByIds(Collection<UUID> factionIds);

    Map<UUID,SectorRef> getSectorsByIds(Collection<UUID> sectorIds);

    Map<String,CurrencyRef> getCurrenciesByIds(Collection<String> currencyCodes);

    Map<UUID,PlanetRef> getPlanetsById(Collection<UUID> planetIds);

    Map<UUID,OrderCategoryRef> getOrderCategoriesById(Collection<UUID> orderCategoryIds);

    Map<UUID,SkillRef> getSkillsById(Collection<UUID> skillIds);

}
