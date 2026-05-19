package com.stud.backend.dictionary.service;

import com.stud.backend.common.exception.DuplicateResourceException;
import com.stud.backend.common.exception.ResourceNotFoundException;
import com.stud.backend.dictionary.domain.Currency;
import com.stud.backend.dictionary.domain.Faction;
import com.stud.backend.dictionary.domain.OrderCategory;
import com.stud.backend.dictionary.domain.Planet;
import com.stud.backend.dictionary.domain.Sector;
import com.stud.backend.dictionary.domain.Skill;
import com.stud.backend.dictionary.repository.CurrencyRepository;
import com.stud.backend.dictionary.repository.FactionRepository;
import com.stud.backend.dictionary.repository.OrderCategoryRepository;
import com.stud.backend.dictionary.repository.PlanetRepository;
import com.stud.backend.dictionary.repository.SectorRepository;
import com.stud.backend.dictionary.repository.SkillRepository;
import com.stud.backend.dictionary.web.dto.DictionaryDtos.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class DictionaryService {

    private final FactionRepository factionRepository;
    private final SectorRepository sectorRepository;
    private final PlanetRepository planetRepository;
    private final OrderCategoryRepository orderCategoryRepository;
    private final CurrencyRepository currencyRepository;
    private final SkillRepository skillRepository;

    public DictionaryService(FactionRepository factionRepository, SectorRepository sectorRepository, PlanetRepository planetRepository, OrderCategoryRepository orderCategoryRepository, CurrencyRepository currencyRepository, SkillRepository skillRepository) {
        this.factionRepository = factionRepository;
        this.sectorRepository = sectorRepository;
        this.planetRepository = planetRepository;
        this.orderCategoryRepository = orderCategoryRepository;
        this.currencyRepository = currencyRepository;
        this.skillRepository = skillRepository;
    }

    public List<FactionResponse> getFactions() {
        return factionRepository.findAll(Sort.by("name"))
                .stream()
                .map(this::toFactionResponse)
                .toList();
    }

    @Transactional
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

        return toFactionResponse(factionRepository.save(faction));
    }

    public List<SectorResponse> getSectors() {
        return sectorRepository.findAll(Sort.by("name"))
                .stream()
                .map(this::toSectorResponse)
                .toList();
    }

    @Transactional
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

        return toSectorResponse(sectorRepository.save(sector));
    }

    public List<PlanetResponse> getPlanets() {
        return planetRepository.findAll(Sort.by("name"))
                .stream()
                .map(this::toPlanetResponse)
                .toList();
    }

    @Transactional
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

        return toPlanetResponse(planetRepository.save(planet));
    }

    public List<OrderCategoryResponse> getOrderCategories() {
        return orderCategoryRepository.findAll(Sort.by("name"))
                .stream()
                .map(this::toOrderCategoryResponse)
                .toList();
    }

    @Transactional
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

        return toOrderCategoryResponse(orderCategoryRepository.save(category));
    }

    public List<CurrencyResponse> getCurrencies() {
        return currencyRepository.findAll(Sort.by("code"))
                .stream()
                .map(this::toCurrencyResponse)
                .toList();
    }

    @Transactional
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

        return toCurrencyResponse(currencyRepository.save(currency));
    }

    public List<SkillResponse> getSkills() {
        return skillRepository.findAll(Sort.by("name"))
                .stream()
                .map(this::toSkillResponse)
                .toList();
    }

    @Transactional
    public SkillResponse createSkill(SkillCreateRequest request) {
        String name = request.name().trim();

        if (skillRepository.existsByNameIgnoreCase(name)) {
            throw new DuplicateResourceException("Skill with name '%s' already exists".formatted(name));
        }

        Skill skill = new Skill();
        skill.setName(name);
        skill.setDescription(request.description());

        return toSkillResponse(skillRepository.save(skill));
    }

    private Faction findFaction(UUID id) {
        return factionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faction not found: " + id));
    }

    private Sector findSector(UUID id) {
        return sectorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sector not found: " + id));
    }

    private FactionResponse toFactionResponse(Faction faction) {
        return new FactionResponse(
                faction.getId(),
                faction.getName(),
                faction.getDescription(),
                faction.getType(),
                faction.getInfluenceLevel(),
                faction.getRelationToGuild()
        );
    }

    private SectorResponse toSectorResponse(Sector sector) {
        UUID factionId = sector.getControllingFaction() == null
                ? null
                : sector.getControllingFaction().getId();

        return new SectorResponse(
                sector.getId(),
                factionId,
                sector.getName(),
                sector.getDescription(),
                sector.getStabilityLevel(),
                sector.getDangerLevel()
        );
    }

    private PlanetResponse toPlanetResponse(Planet planet) {
        UUID sectorId = planet.getSector() == null
                ? null
                : planet.getSector().getId();

        UUID factionId = planet.getControllingFaction() == null
                ? null
                : planet.getControllingFaction().getId();

        return new PlanetResponse(
                planet.getId(),
                sectorId,
                factionId,
                planet.getName(),
                planet.getDescription(),
                planet.getDangerLevel(),
                planet.getDevelopmentLevel(),
                planet.getClimate(),
                planet.getPopulation(),
                planet.getStatus()
        );
    }

    private OrderCategoryResponse toOrderCategoryResponse(OrderCategory category) {
        return new OrderCategoryResponse(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getDescription(),
                category.getActive()
        );
    }

    private CurrencyResponse toCurrencyResponse(Currency currency) {
        return new CurrencyResponse(
                currency.getCode(),
                currency.getName(),
                currency.getSymbol(),
                currency.getActive()
        );
    }

    private SkillResponse toSkillResponse(Skill skill) {
        return new SkillResponse(
                skill.getId(),
                skill.getName(),
                skill.getDescription()
        );
    }
}