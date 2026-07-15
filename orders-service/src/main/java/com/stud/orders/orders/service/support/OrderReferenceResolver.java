package com.stud.orders.orders.service.support;

import com.stud.orders.common.exception.BadRequestException;
import com.stud.orders.integrations.dictionary.CurrencyRef;
import com.stud.orders.integrations.dictionary.DictionaryLookup;
import com.stud.orders.integrations.dictionary.OrderCategoryRef;
import com.stud.orders.integrations.dictionary.PlanetRef;
import com.stud.orders.integrations.dictionary.SectorRef;
import com.stud.orders.integrations.profiles.ClientProfileRef;
import com.stud.orders.integrations.profiles.HunterProfileRef;
import com.stud.orders.integrations.profiles.ProfileLookup;
import com.stud.orders.integrations.users.UserLookup;
import com.stud.orders.integrations.users.UserRef;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

// работа с user/profile/dictionary lookup, активные категории/валюты, batch-загрузка справочников

@Component
@RequiredArgsConstructor
public class OrderReferenceResolver {

    private final DictionaryLookup dictionaryLookup;
    private final ProfileLookup profileLookup;
    private final UserLookup userLookup;

    public ClientProfileRef currentClientProfile(String email) {
        UserRef user = userLookup.getByEmail(email);

        return profileLookup.getClientProfileByUserId(user.id());
    }

    public HunterProfileRef currentHunterProfile(String email) {
        UserRef user = userLookup.getByEmail(email);

        return profileLookup.getHunterProfileByUserId(user.id());
    }

    public ClientProfileRef clientProfile(UUID clientProfileId) {
        return profileLookup.getClientProfile(clientProfileId);
    }

    public HunterProfileRef hunterProfile(UUID hunterProfileId) {
        return profileLookup.getHunterProfile(hunterProfileId);
    }

    public OrderCategoryRef category(UUID categoryId) {
        return dictionaryLookup.getOrderCategory(categoryId);
    }

    public OrderCategoryRef activeCategory(UUID categoryId) {
        OrderCategoryRef category = category(categoryId);

        if (!Boolean.TRUE.equals(category.active())) {
            throw new BadRequestException("Category is not active " + categoryId);
        }

        return category;
    }

    public CurrencyRef currency(String currencyCode) {
        return dictionaryLookup.getCurrency(currencyCode);
    }

    public CurrencyRef activeCurrency(String currencyCode) {
        CurrencyRef currency = currency(currencyCode);

        if (!Boolean.TRUE.equals(currency.active())) {
            throw new BadRequestException("Currency is not active " + currencyCode);
        }

        return currency;
    }

    public PlanetRef planet(UUID planetId) {
        return dictionaryLookup.getPlanet(planetId);
    }

    public SectorRef sector(UUID sectorId) {
        return dictionaryLookup.getSector(sectorId);
    }

    public Map<UUID, ClientProfileRef> clientProfilesByIds(Collection<UUID> clientProfileIds) {
        Set<UUID> ids = uuidSet(clientProfileIds);

        if (ids.isEmpty()) {
            return Map.of();
        }

        return profileLookup.getClientProfilesByIds(ids);
    }

    public Map<UUID, HunterProfileRef> hunterProfilesByIds(Collection<UUID> hunterProfileIds) {
        Set<UUID> ids = uuidSet(hunterProfileIds);

        if (ids.isEmpty()) {
            return Map.of();
        }

        return profileLookup.getHunterProfilesByIds(ids);
    }

    public Map<UUID, OrderCategoryRef> categoriesByIds(Collection<UUID> categoryIds) {
        Set<UUID> ids = uuidSet(categoryIds);

        if (ids.isEmpty()) {
            return Map.of();
        }

        return dictionaryLookup.getOrderCategoriesById(ids);
    }

    public Map<String, CurrencyRef> currenciesByCodes(Collection<String> currencyCodes) {
        Set<String> codes = stringSet(currencyCodes);

        if (codes.isEmpty()) {
            return Map.of();
        }

        return dictionaryLookup.getCurrenciesByIds(codes);
    }

    public Map<UUID, PlanetRef> planetsByIds(Collection<UUID> planetIds) {
        Set<UUID> ids = uuidSet(planetIds);

        if (ids.isEmpty()) {
            return Map.of();
        }

        return dictionaryLookup.getPlanetsById(ids);
    }

    public Map<UUID, SectorRef> sectorsByIds(Collection<UUID> sectorIds) {
        Set<UUID> ids = uuidSet(sectorIds);

        if (ids.isEmpty()) {
            return Map.of();
        }

        return dictionaryLookup.getSectorsByIds(ids);
    }

    private Set<UUID> uuidSet(Collection<UUID> ids) {
        return ids.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<String> stringSet(Collection<String> values) {
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.toSet());
    }
}
