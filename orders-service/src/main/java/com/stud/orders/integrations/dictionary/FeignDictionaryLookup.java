package com.stud.orders.integrations.dictionary;

import com.stud.orders.common.exception.ResourceNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeignDictionaryLookup implements DictionaryLookup {

    private final DictionaryServiceFeignClient client;

    @Override
    public OrderCategoryRef getOrderCategory(UUID categoryId) {
        return get(() -> client.getOrderCategory(categoryId), "Order category not found: " + categoryId);
    }

    @Override
    public CurrencyRef getCurrency(String currencyCode) {
        String code = normalizeCode(currencyCode);
        return get(() -> client.getCurrency(code), "Currency not found: " + code);
    }

    @Override
    public PlanetRef getPlanet(UUID planetId) {
        return get(() -> client.getPlanet(planetId), "Planet not found: " + planetId);
    }

    @Override
    public SectorRef getSector(UUID sectorId) {
        return get(() -> client.getSector(sectorId), "Sector not found: " + sectorId);
    }

    @Override
    public Map<UUID, OrderCategoryRef> getOrderCategoriesById(Collection<UUID> orderCategoryIds) {
        List<UUID> ids = normalizeUuids(orderCategoryIds);
        if (ids.isEmpty()) {
            return Map.of();
        }
        return toMap(client.getOrderCategories(new DictionaryServiceFeignClient.UuidLookupRequest(ids)), OrderCategoryRef::id);
    }

    @Override
    public Map<String, CurrencyRef> getCurrenciesByIds(Collection<String> currencyCodes) {
        List<String> codes = normalizeCodes(currencyCodes);
        if (codes.isEmpty()) {
            return Map.of();
        }
        return toMap(client.getCurrencies(new DictionaryServiceFeignClient.CurrencyLookupRequest(codes)), CurrencyRef::code);
    }

    @Override
    public Map<UUID, PlanetRef> getPlanetsById(Collection<UUID> planetIds) {
        List<UUID> ids = normalizeUuids(planetIds);
        if (ids.isEmpty()) {
            return Map.of();
        }
        return toMap(client.getPlanets(new DictionaryServiceFeignClient.UuidLookupRequest(ids)), PlanetRef::id);
    }

    @Override
    public Map<UUID, SectorRef> getSectorsByIds(Collection<UUID> sectorIds) {
        List<UUID> ids = normalizeUuids(sectorIds);
        if (ids.isEmpty()) {
            return Map.of();
        }
        return toMap(client.getSectors(new DictionaryServiceFeignClient.UuidLookupRequest(ids)), SectorRef::id);
    }

    private <T> T get(Supplier<T> request, String notFoundMessage) {
        try {
            return request.get();
        } catch (FeignException.NotFound ex) {
            throw new ResourceNotFoundException(notFoundMessage);
        }
    }

    private List<UUID> normalizeUuids(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return ids.stream().filter(Objects::nonNull).distinct().toList();
    }

    private List<String> normalizeCodes(Collection<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return List.of();
        }
        return codes.stream().filter(Objects::nonNull).map(this::normalizeCode).distinct().toList();
    }

    private <K, V> Map<K, V> toMap(List<V> refs, Function<V, K> keyExtractor) {
        if (refs == null || refs.isEmpty()) {
            return Map.of();
        }
        return refs.stream().collect(Collectors.toMap(keyExtractor, Function.identity()));
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
    }
}
