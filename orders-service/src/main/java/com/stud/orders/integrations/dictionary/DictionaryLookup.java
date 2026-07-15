package com.stud.orders.integrations.dictionary;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface DictionaryLookup {

    OrderCategoryRef getOrderCategory(UUID categoryId);

    CurrencyRef getCurrency(String currencyCode);

    PlanetRef getPlanet(UUID planetId);

    SectorRef getSector(UUID sectorId);

    Map<UUID, OrderCategoryRef> getOrderCategoriesById(Collection<UUID> orderCategoryIds);

    Map<String, CurrencyRef> getCurrenciesByIds(Collection<String> currencyCodes);

    Map<UUID, PlanetRef> getPlanetsById(Collection<UUID> planetIds);

    Map<UUID, SectorRef> getSectorsByIds(Collection<UUID> sectorIds);
}
