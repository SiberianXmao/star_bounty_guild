package com.stud.dictionary.service.impl;

import com.stud.dictionary.common.exception.DuplicateResourceException;
import com.stud.dictionary.common.exception.ResourceNotFoundException;
import com.stud.dictionary.domain.Currency;
import com.stud.dictionary.domain.Faction;
import com.stud.dictionary.domain.OrderCategory;
import com.stud.dictionary.domain.Planet;
import com.stud.dictionary.domain.Sector;
import com.stud.dictionary.domain.Skill;
import com.stud.dictionary.mapper.DictionaryResponseMapper;
import com.stud.dictionary.repository.CurrencyRepository;
import com.stud.dictionary.repository.FactionRepository;
import com.stud.dictionary.repository.OrderCategoryRepository;
import com.stud.dictionary.repository.PlanetRepository;
import com.stud.dictionary.repository.SectorRepository;
import com.stud.dictionary.repository.SkillRepository;
import com.stud.dictionary.service.DictionaryService;
import com.stud.dictionary.web.dto.DictionaryDtos.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.stud.dictionary.config.cache.DictionaryCacheNames.CURRENCIES;
import static com.stud.dictionary.config.cache.DictionaryCacheNames.FACTIONS;
import static com.stud.dictionary.config.cache.DictionaryCacheNames.ORDER_CATEGORIES;
import static com.stud.dictionary.config.cache.DictionaryCacheNames.PLANETS;
import static com.stud.dictionary.config.cache.DictionaryCacheNames.SECTORS;
import static com.stud.dictionary.config.cache.DictionaryCacheNames.SKILLS;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DictionaryServiceImpl implements DictionaryService {

    private final FactionRepository factionRepository;
    private final SectorRepository sectorRepository;
    private final PlanetRepository planetRepository;
    private final OrderCategoryRepository orderCategoryRepository;
    private final CurrencyRepository currencyRepository;
    private final SkillRepository skillRepository;
    private final DictionaryResponseMapper mapper;

    @Override
    @Cacheable(cacheNames = FACTIONS, key = "'all'")
    public List<FactionResponse> getFactions() {
        return factionRepository.findAll(Sort.by("name"))
                .stream()
                .map(mapper::toFactionResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = FACTIONS, allEntries = true)
    public FactionResponse createFaction(FactionCreateRequest request) {
        String name = request.name().trim();

        if (factionRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Faction with name '%s' already exists".formatted(name));
        }

        Faction faction = new Faction();
        faction.setName(name);
        faction.setDescription(request.description());
        faction.setType(request.type());
        faction.setInfluenceLevel(request.influenceLevel());
        faction.setRelationToGuild(request.relationToGuild());

        return mapper.toFactionResponse(factionRepository.save(faction));
    }

    @Override
    @Cacheable(cacheNames = SECTORS, key = "'all'")
    public List<SectorResponse> getSectors() {
        return sectorRepository.findAll(Sort.by("name"))
                .stream()
                .map(mapper::toSectorResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = SECTORS, allEntries = true)
    public SectorResponse createSector(SectorCreateRequest request) {
        String name = request.name().trim();

        if (sectorRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Sector with name '%s' already exists".formatted(name));
        }

        Sector sector = new Sector();
        sector.setName(name);
        sector.setDescription(request.description());
        sector.setStabilityLevel(request.stabilityLevel());
        sector.setDangerLevel(request.dangerLevel());

        if (request.controllingFactionId() != null) {
            sector.setControllingFaction(findFaction(request.controllingFactionId()));
        }

        return mapper.toSectorResponse(sectorRepository.save(sector));
    }

    @Override
    @Cacheable(cacheNames = PLANETS, key = "'all'")
    public List<PlanetResponse> getPlanets() {
        return planetRepository.findAll(Sort.by("name"))
                .stream()
                .map(mapper::toPlanetResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = PLANETS, allEntries = true)
    public PlanetResponse createPlanet(PlanetCreateRequest request) {
        String name = request.name().trim();

        if (planetRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Planet with name '%s' already exists".formatted(name));
        }

        Planet planet = new Planet();
        planet.setName(name);
        planet.setDescription(request.description());
        planet.setDangerLevel(request.dangerLevel());
        planet.setDevelopmentLevel(request.developmentLevel());
        planet.setClimate(request.climate());
        planet.setPopulation(request.population());
        planet.setStatus(request.status());
        planet.setSector(findSector(request.sectorId()));

        if (request.controllingFactionId() != null) {
            planet.setControllingFaction(findFaction(request.controllingFactionId()));
        }

        return mapper.toPlanetResponse(planetRepository.save(planet));
    }

    @Override
    @Cacheable(cacheNames = ORDER_CATEGORIES, key = "'all'")
    public List<OrderCategoryResponse> getOrderCategories() {
        return orderCategoryRepository.findAll(Sort.by("name"))
                .stream()
                .map(mapper::toOrderCategoryResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = ORDER_CATEGORIES, allEntries = true)
    public OrderCategoryResponse createOrderCategory(OrderCategoryCreateRequest request) {
        String slug = request.slug().trim().toLowerCase();

        if (orderCategoryRepository.existsBySlugIgnoreCase(slug)) {
            throw new DuplicateResourceException("Order category with slug '%s' already exists".formatted(slug));
        }

        OrderCategory category = new OrderCategory();
        category.setName(request.name().trim());
        category.setSlug(slug);
        category.setDescription(request.description());
        category.setActive(request.active() == null || request.active());

        return mapper.toOrderCategoryResponse(orderCategoryRepository.save(category));
    }

    @Override
    @Cacheable(cacheNames = CURRENCIES, key = "'all'")
    public List<CurrencyResponse> getCurrencies() {
        return currencyRepository.findAll(Sort.by("code"))
                .stream()
                .map(mapper::toCurrencyResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CURRENCIES, allEntries = true)
    public CurrencyResponse createCurrency(CurrencyCreateRequest request) {
        String code = request.code().trim().toUpperCase();

        if (currencyRepository.existsByCodeIgnoreCase(code)) {
            throw new DuplicateResourceException("Currency with code '%s' already exists".formatted(code));
        }

        Currency currency = new Currency();
        currency.setCode(code);
        currency.setName(request.name().trim());
        currency.setSymbol(request.symbol());
        currency.setActive(request.active() == null || request.active());

        return mapper.toCurrencyResponse(currencyRepository.save(currency));
    }

    @Override
    @Cacheable(cacheNames = SKILLS, key = "'all'")
    public List<SkillResponse> getSkills() {
        return skillRepository.findAll(Sort.by("name"))
                .stream()
                .map(mapper::toSkillResponse)
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = SKILLS, allEntries = true)
    public SkillResponse createSkill(SkillCreateRequest request) {
        String name = request.name().trim();

        if (skillRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Skill with name '%s' already exists".formatted(name));
        }

        Skill skill = new Skill();
        skill.setName(name);
        skill.setDescription(request.description());

        return mapper.toSkillResponse(skillRepository.save(skill));
    }

    private Faction findFaction(UUID id) {
        return factionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faction not found: " + id));
    }

    private Sector findSector(UUID id) {
        return sectorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sector not found: " + id));
    }

}
