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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.debug("Loading faction dictionary");

        List<FactionResponse> factions = factionRepository.findAll(Sort.by("name"))
                .stream()
                .map(mapper::toFactionResponse)
                .toList();

        log.debug("Loaded faction dictionary size={}", factions.size());
        return factions;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = FACTIONS, allEntries = true)
    public FactionResponse createFaction(FactionCreateRequest request) {
        String name = request.name().trim();

        log.info("Creating faction name='{}' type={} influenceLevel={}", name, request.type(), request.influenceLevel());

        if (factionRepository.existsByNameIgnoreCase(name)) {
            log.warn("Faction creation rejected because name already exists name='{}'", name);
            throw new DuplicateResourceException("Faction with name '%s' already exists".formatted(name));
        }

        Faction faction = new Faction();
        faction.setName(name);
        faction.setDescription(request.description());
        faction.setType(request.type());
        faction.setInfluenceLevel(request.influenceLevel());
        faction.setRelationToGuild(request.relationToGuild());

        Faction saved = factionRepository.save(faction);
        log.info("Created faction id={} name='{}'", saved.getId(), saved.getName());

        return mapper.toFactionResponse(saved);
    }

    @Override
    @Cacheable(cacheNames = SECTORS, key = "'all'")
    public List<SectorResponse> getSectors() {
        log.debug("Loading sector dictionary");

        List<SectorResponse> sectors = sectorRepository.findAll(Sort.by("name"))
                .stream()
                .map(mapper::toSectorResponse)
                .toList();

        log.debug("Loaded sector dictionary size={}", sectors.size());
        return sectors;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = SECTORS, allEntries = true)
    public SectorResponse createSector(SectorCreateRequest request) {
        String name = request.name().trim();

        log.info(
                "Creating sector name='{}' stabilityLevel={} dangerLevel={} controllingFactionId={}",
                name,
                request.stabilityLevel(),
                request.dangerLevel(),
                request.controllingFactionId()
        );

        if (sectorRepository.existsByNameIgnoreCase(name)) {
            log.warn("Sector creation rejected because name already exists name='{}'", name);
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

        Sector saved = sectorRepository.save(sector);
        log.info("Created sector id={} name='{}'", saved.getId(), saved.getName());

        return mapper.toSectorResponse(saved);
    }

    @Override
    @Cacheable(cacheNames = PLANETS, key = "'all'")
    public List<PlanetResponse> getPlanets() {
        log.debug("Loading planet dictionary");

        List<PlanetResponse> planets = planetRepository.findAll(Sort.by("name"))
                .stream()
                .map(mapper::toPlanetResponse)
                .toList();

        log.debug("Loaded planet dictionary size={}", planets.size());
        return planets;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = PLANETS, allEntries = true)
    public PlanetResponse createPlanet(PlanetCreateRequest request) {
        String name = request.name().trim();

        log.info(
                "Creating planet name='{}' sectorId={} controllingFactionId={} dangerLevel={} status={}",
                name,
                request.sectorId(),
                request.controllingFactionId(),
                request.dangerLevel(),
                request.status()
        );

        if (planetRepository.existsByNameIgnoreCase(name)) {
            log.warn("Planet creation rejected because name already exists name='{}'", name);
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

        Planet saved = planetRepository.save(planet);
        log.info("Created planet id={} name='{}' sectorId={}", saved.getId(), saved.getName(), saved.getSector().getId());

        return mapper.toPlanetResponse(saved);
    }

    @Override
    @Cacheable(cacheNames = ORDER_CATEGORIES, key = "'all'")
    public List<OrderCategoryResponse> getOrderCategories() {
        log.debug("Loading order category dictionary");

        List<OrderCategoryResponse> categories = orderCategoryRepository.findAll(Sort.by("name"))
                .stream()
                .map(mapper::toOrderCategoryResponse)
                .toList();

        log.debug("Loaded order category dictionary size={}", categories.size());
        return categories;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = ORDER_CATEGORIES, allEntries = true)
    public OrderCategoryResponse createOrderCategory(OrderCategoryCreateRequest request) {
        String slug = request.slug().trim().toLowerCase();

        log.info("Creating order category name='{}' slug='{}' active={}", request.name(), slug, request.active());

        if (orderCategoryRepository.existsBySlugIgnoreCase(slug)) {
            log.warn("Order category creation rejected because slug already exists slug='{}'", slug);
            throw new DuplicateResourceException("Order category with slug '%s' already exists".formatted(slug));
        }

        OrderCategory category = new OrderCategory();
        category.setName(request.name().trim());
        category.setSlug(slug);
        category.setDescription(request.description());
        category.setActive(request.active() == null || request.active());

        OrderCategory saved = orderCategoryRepository.save(category);
        log.info("Created order category id={} slug='{}'", saved.getId(), saved.getSlug());

        return mapper.toOrderCategoryResponse(saved);
    }

    @Override
    @Cacheable(cacheNames = CURRENCIES, key = "'all'")
    public List<CurrencyResponse> getCurrencies() {
        log.debug("Loading currency dictionary");

        List<CurrencyResponse> currencies = currencyRepository.findAll(Sort.by("code"))
                .stream()
                .map(mapper::toCurrencyResponse)
                .toList();

        log.debug("Loaded currency dictionary size={}", currencies.size());
        return currencies;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = CURRENCIES, allEntries = true)
    public CurrencyResponse createCurrency(CurrencyCreateRequest request) {
        String code = request.code().trim().toUpperCase();

        log.info("Creating currency code='{}' name='{}' active={}", code, request.name(), request.active());

        if (currencyRepository.existsByCodeIgnoreCase(code)) {
            log.warn("Currency creation rejected because code already exists code='{}'", code);
            throw new DuplicateResourceException("Currency with code '%s' already exists".formatted(code));
        }

        Currency currency = new Currency();
        currency.setCode(code);
        currency.setName(request.name().trim());
        currency.setSymbol(request.symbol());
        currency.setActive(request.active() == null || request.active());

        Currency saved = currencyRepository.save(currency);
        log.info("Created currency code='{}' name='{}'", saved.getCode(), saved.getName());

        return mapper.toCurrencyResponse(saved);
    }

    @Override
    @Cacheable(cacheNames = SKILLS, key = "'all'")
    public List<SkillResponse> getSkills() {
        log.debug("Loading skill dictionary");

        List<SkillResponse> skills = skillRepository.findAll(Sort.by("name"))
                .stream()
                .map(mapper::toSkillResponse)
                .toList();

        log.debug("Loaded skill dictionary size={}", skills.size());
        return skills;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = SKILLS, allEntries = true)
    public SkillResponse createSkill(SkillCreateRequest request) {
        String name = request.name().trim();

        log.info("Creating skill name='{}'", name);

        if (skillRepository.existsByNameIgnoreCase(name)) {
            log.warn("Skill creation rejected because name already exists name='{}'", name);
            throw new DuplicateResourceException("Skill with name '%s' already exists".formatted(name));
        }

        Skill skill = new Skill();
        skill.setName(name);
        skill.setDescription(request.description());

        Skill saved = skillRepository.save(skill);
        log.info("Created skill id={} name='{}'", saved.getId(), saved.getName());

        return mapper.toSkillResponse(saved);
    }

    private Faction findFaction(UUID id) {
        log.debug("Resolving faction id={}", id);

        return factionRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Faction lookup failed because faction was not found id={}", id);
                    return new ResourceNotFoundException("Faction not found: " + id);
                });
    }

    private Sector findSector(UUID id) {
        log.debug("Resolving sector id={}", id);

        return sectorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Sector lookup failed because sector was not found id={}", id);
                    return new ResourceNotFoundException("Sector not found: " + id);
                });
    }

}
