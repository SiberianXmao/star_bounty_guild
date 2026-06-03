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

import java.util.UUID;

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

        return new FactionRef(
                faction.getId(),
                faction.getName(),
                faction.getType().name(),
                faction.getRelationToGuild().name()
        );
    }

    @Override
    public SectorRef getSector(UUID sectorId) {
        Sector sector = sectorRepository.findById(sectorId)
                .orElseThrow(() -> new ResourceNotFoundException("Sector not found: " + sectorId));

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

    @Override
    public PlanetRef getPlanet(UUID planetId) {
        Planet planet = planetRepository.findById(planetId)
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found: " + planetId));

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

    @Override
    public CurrencyRef getCurrency(String currencyCode) {
        String code = currencyCode.trim().toUpperCase();
        Currency currency = currencyRepository.findById(code)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found: " + code));

        return new CurrencyRef(
                currency.getCode(),
                currency.getName(),
                currency.getSymbol(),
                currency.getActive()
        );
    }

    @Override
    public OrderCategoryRef getOrderCategory(UUID categoryId) {
        OrderCategory category = orderCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Order category not found: " + categoryId));

        return new OrderCategoryRef(
                category.getId(),
                category.getName(),
                category.getSlug(),
                category.getActive()
        );
    }

    @Override
    public SkillRef getSkill(UUID skillId) {
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillId));

        return new SkillRef(
                skill.getId(),
                skill.getName()
        );
    }
}
