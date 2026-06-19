package com.stud.dictionary.web;

import com.stud.dictionary.service.DictionaryService;
import com.stud.dictionary.web.dto.DictionaryDtos.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/dictionary")
public class DictionaryController {

    private final DictionaryService dictionaryService;

    public DictionaryController(DictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @GetMapping("/factions")
    public List<FactionResponse> getFactions() {
        return dictionaryService.getFactions();
    }

    @PostMapping("/factions")
    @ResponseStatus(HttpStatus.CREATED)
    public FactionResponse createFaction(@Valid @RequestBody FactionCreateRequest request) {
        return dictionaryService.createFaction(request);
    }

    @GetMapping("/sectors")
    public List<SectorResponse> getSectors() {
        return dictionaryService.getSectors();
    }

    @PostMapping("/sectors")
    @ResponseStatus(HttpStatus.CREATED)
    public SectorResponse createSector(@Valid @RequestBody SectorCreateRequest request) {
        return dictionaryService.createSector(request);
    }

    @GetMapping("/planets")
    public List<PlanetResponse> getPlanets() {
        return dictionaryService.getPlanets();
    }

    @PostMapping("/planets")
    @ResponseStatus(HttpStatus.CREATED)
    public PlanetResponse createPlanet(@Valid @RequestBody PlanetCreateRequest request) {
        return dictionaryService.createPlanet(request);
    }

    @GetMapping("/order-categories")
    public List<OrderCategoryResponse> getOrderCategories() {
        return dictionaryService.getOrderCategories();
    }

    @PostMapping("/order-categories")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderCategoryResponse createOrderCategory(@Valid @RequestBody OrderCategoryCreateRequest request) {
        return dictionaryService.createOrderCategory(request);
    }

    @GetMapping("/currencies")
    public List<CurrencyResponse> getCurrencies() {
        return dictionaryService.getCurrencies();
    }

    @PostMapping("/currencies")
    @ResponseStatus(HttpStatus.CREATED)
    public CurrencyResponse createCurrency(@Valid @RequestBody CurrencyCreateRequest request) {
        return dictionaryService.createCurrency(request);
    }

    @GetMapping("/skills")
    public List<SkillResponse> getSkills() {
        return dictionaryService.getSkills();
    }

    @PostMapping("/skills")
    @ResponseStatus(HttpStatus.CREATED)
    public SkillResponse createSkill(@Valid @RequestBody SkillCreateRequest request) {
        return dictionaryService.createSkill(request);
    }
}
