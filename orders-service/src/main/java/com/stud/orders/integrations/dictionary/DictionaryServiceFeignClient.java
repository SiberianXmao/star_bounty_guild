package com.stud.orders.integrations.dictionary;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "dictionary-service",
        url = "${app.services.dictionary.base-url}",
        path = "/internal/v1/dictionary"
)
public interface DictionaryServiceFeignClient {

    @GetMapping("/order-categories/{id}")
    OrderCategoryRef getOrderCategory(@PathVariable UUID id);

    @PostMapping("/order-categories/lookup")
    List<OrderCategoryRef> getOrderCategories(@RequestBody UuidLookupRequest request);

    @GetMapping("/currencies/{code}")
    CurrencyRef getCurrency(@PathVariable String code);

    @PostMapping("/currencies/lookup")
    List<CurrencyRef> getCurrencies(@RequestBody CurrencyLookupRequest request);

    @GetMapping("/planets/{id}")
    PlanetRef getPlanet(@PathVariable UUID id);

    @PostMapping("/planets/lookup")
    List<PlanetRef> getPlanets(@RequestBody UuidLookupRequest request);

    @GetMapping("/sectors/{id}")
    SectorRef getSector(@PathVariable UUID id);

    @PostMapping("/sectors/lookup")
    List<SectorRef> getSectors(@RequestBody UuidLookupRequest request);

    record UuidLookupRequest(Collection<UUID> ids) {
    }

    record CurrencyLookupRequest(Collection<String> codes) {
    }
}
