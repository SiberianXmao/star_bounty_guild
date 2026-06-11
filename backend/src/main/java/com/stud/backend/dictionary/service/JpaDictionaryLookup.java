package com.stud.backend.dictionary.service;

import com.stud.backend.common.exception.ResourceNotFoundException;
import com.stud.backend.dictionary.api.CurrencyRef;
import com.stud.backend.dictionary.api.DictionaryLookup;
import com.stud.backend.dictionary.api.FactionRef;
import com.stud.backend.dictionary.api.OrderCategoryRef;
import com.stud.backend.dictionary.api.PlanetRef;
import com.stud.backend.dictionary.api.SectorRef;
import com.stud.backend.dictionary.api.SkillRef;
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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaDictionaryLookup implements DictionaryLookup {

    private final FactionRepository factionRepository;
    private final SectorRepository sectorRepository;
    private final PlanetRepository planetRepository;
    private final CurrencyRepository currencyRepository;
    private final OrderCategoryRepository orderCategoryRepository;
    private final SkillRepository skillRepository;

    @Override
    public FactionRef getFaction(UUID factionId) {
        Faction faction = factionRepository.findById(factionId)
                .orElseThrow(() -> new ResourceNotFoundException("Faction not found: " + factionId));

        return toFactionRef(faction);
    }

    @Override
    public SectorRef getSector(UUID sectorId) {
        Sector sector = sectorRepository.findById(sectorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sector not found: " + sectorId));

        return toSectorRef(sector);
    }

    @Override
    public PlanetRef getPlanet(UUID planetId) {
        Planet planet = planetRepository.findById(planetId)
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found: " + planetId));

        return toPlanetRef(planet);
    }

    @Override
    public CurrencyRef getCurrency(String currencyCode) {
        String code = currencyCode.trim().toUpperCase();
        Currency currency = currencyRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found: " + code));

        return toCurrencyRef(currency);
    }

    @Override
    public OrderCategoryRef getOrderCategory(UUID categoryId) {
        OrderCategory category = orderCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Order category not found: " + categoryId));

        return toOrderCategoryRef(category);
    }

    @Override
    public SkillRef getSkill(UUID skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillId));

        return toSkillRef(skill);
    }

    @Override
    public Map<UUID, FactionRef> getFactionsByIds(Collection<UUID> factionIds) {
        if (factionIds == null || factionIds.isEmpty()) {
            return Map.of();
        }
        return factionRepository.findAllById(factionIds)
                .stream()
                .collect(Collectors.toMap(
                        Faction::getId,
                        this::toFactionRef
                ));
    }

    @Override
    public Map<UUID, SkillRef> getSkillsById(Collection<UUID> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return Map.of();
        }

        return skillRepository.findAllById(skillIds)
                .stream()
                .collect(Collectors.toMap(
                        Skill::getId,
                        this::toSkillRef
                ));
    }

    @Override
    public Map<UUID, OrderCategoryRef> getOrderCategoriesById(Collection<UUID> orderCategoryIds) {
        if (orderCategoryIds == null || orderCategoryIds.isEmpty()) {
            return Map.of();
        }

        return orderCategoryRepository.findAllById(orderCategoryIds)
                .stream()
                .collect(Collectors.toMap(
                        OrderCategory::getId,
                        this::toOrderCategoryRef
                ));
    }

    @Override
    public Map<UUID, PlanetRef> getPlanetsById(Collection<UUID> planetIds) {
        if  (planetIds == null || planetIds.isEmpty()) {
            return Map.of();
        }
        return planetRepository.findAllById(planetIds)
                .stream()
                .collect(Collectors.toMap(
                        Planet::getId,
                        this::toPlanetRef
                ));
    }

    @Override
    public Map<String, CurrencyRef> getCurrenciesByIds(Collection<String> currencyCodes) {
        if (currencyCodes == null || currencyCodes.isEmpty()) {
            return Map.of();
        }
        List<String> normalizedCodes = currencyCodes.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .map(String::toUpperCase)
                .distinct()
                .toList();

        return currencyRepository.findAllById(normalizedCodes)
                .stream()
                .collect(Collectors.toMap(
                        Currency::getCode,
                        this::toCurrencyRef
                ));

    }

    @Override
    public Map<UUID, SectorRef> getSectorsByIds(Collection<UUID> sectorIds) {
        if (sectorIds == null || sectorIds.isEmpty()) {
            return Map.of();
        }

        return sectorRepository.findAllById(sectorIds)
                .stream()
                .collect(Collectors.toMap(
                        Sector::getId,
                        this::toSectorRef
                ));
    }

    private FactionRef toFactionRef(Faction faction) {
        return new FactionRef(
                faction.getId(),
                faction.getName(),
                faction.getType().name(),
                faction.getRelationToGuild().name()
        );
    }

    private PlanetRef toPlanetRef(Planet planet) {

        UUID sectorId = planet.getSector() == null
                ? null
                : planet.getSector().getId();

        UUID controllingFactionId = planet.getControllingFaction() == null
                ? null
                : planet.getControllingFaction().getId();

        return new PlanetRef(
                planet.getId(),
                sectorId,
                controllingFactionId,
                planet.getName(),
                planet.getDangerLevel(),
                planet.getDevelopmentLevel(),
                planet.getStatus().name()
        );
    }

    private SectorRef toSectorRef(Sector sector) {
        UUID controllingFactionId = sector.getControllingFaction() == null
                ? null
                : sector.getControllingFaction().getId();

        return new SectorRef(
                sector.getId(),
                controllingFactionId,
                sector.getName(),
                sector.getStabilityLevel(),
                sector.getDangerLevel()
        );
    }

    private CurrencyRef toCurrencyRef(Currency currency) {
        return new CurrencyRef(
                currency.getCode(),
                currency.getName(),
                currency.getSymbol(),
                currency.getActive()
        );
    }

    private OrderCategoryRef toOrderCategoryRef(OrderCategory category) {
        return new OrderCategoryRef(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getActive()
        );
    }

    private SkillRef toSkillRef(Skill skill) {
        return new SkillRef(
                skill.getId(),
                skill.getName()
        );
    }
}
