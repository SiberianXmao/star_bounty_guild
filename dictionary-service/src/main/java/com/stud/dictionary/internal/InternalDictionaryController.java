package com.stud.dictionary.internal;

import com.stud.dictionary.internal.dto.DictionaryLookupDtos.CurrencyLookupRequest;
import com.stud.dictionary.internal.dto.DictionaryLookupDtos.CurrencyRef;
import com.stud.dictionary.internal.dto.DictionaryLookupDtos.FactionRef;
import com.stud.dictionary.internal.dto.DictionaryLookupDtos.OrderCategoryRef;
import com.stud.dictionary.internal.dto.DictionaryLookupDtos.PlanetRef;
import com.stud.dictionary.internal.dto.DictionaryLookupDtos.SectorRef;
import com.stud.dictionary.internal.dto.DictionaryLookupDtos.SkillRef;
import com.stud.dictionary.internal.dto.DictionaryLookupDtos.UuidLookupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/v1/dictionary")
public class InternalDictionaryController {

    private final DictionaryLookupService dictionaryLookupService;

    @GetMapping("/factions/{id}")
    public FactionRef getFaction(@PathVariable UUID id) {
        return dictionaryLookupService.getFaction(id);
    }

    @PostMapping("/factions/lookup")
    public List<FactionRef> getFactions(@RequestBody UuidLookupRequest request) {
        return dictionaryLookupService.getFactions(request.ids());
    }

    @GetMapping("/sectors/{id}")
    public SectorRef getSector(@PathVariable UUID id) {
        return dictionaryLookupService.getSector(id);
    }

    @PostMapping("/sectors/lookup")
    public List<SectorRef> getSectors(@RequestBody UuidLookupRequest request) {
        return dictionaryLookupService.getSectors(request.ids());
    }

    @GetMapping("/planets/{id}")
    public PlanetRef getPlanet(@PathVariable UUID id) {
        return dictionaryLookupService.getPlanet(id);
    }

    @PostMapping("/planets/lookup")
    public List<PlanetRef> getPlanets(@RequestBody UuidLookupRequest request) {
        return dictionaryLookupService.getPlanets(request.ids());
    }

    @GetMapping("/currencies/{code}")
    public CurrencyRef getCurrency(@PathVariable String code) {
        return dictionaryLookupService.getCurrency(code);
    }

    @PostMapping("/currencies/lookup")
    public List<CurrencyRef> getCurrencies(@RequestBody CurrencyLookupRequest request) {
        return dictionaryLookupService.getCurrencies(request.codes());
    }

    @GetMapping("/order-categories/{id}")
    public OrderCategoryRef getOrderCategory(@PathVariable UUID id) {
        return dictionaryLookupService.getOrderCategory(id);
    }

    @PostMapping("/order-categories/lookup")
    public List<OrderCategoryRef> getOrderCategories(@RequestBody UuidLookupRequest request) {
        return dictionaryLookupService.getOrderCategories(request.ids());
    }

    @GetMapping("/skills/{id}")
    public SkillRef getSkill(@PathVariable UUID id) {
        return dictionaryLookupService.getSkill(id);
    }

    @PostMapping("/skills/lookup")
    public List<SkillRef> getSkills(@RequestBody UuidLookupRequest request) {
        return dictionaryLookupService.getSkills(request.ids());
    }
}
