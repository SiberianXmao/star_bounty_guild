package com.stud.dictionary.internal;

import com.stud.dictionary.common.exception.ResourceNotFoundException;
import com.stud.dictionary.domain.Currency;
import com.stud.dictionary.domain.Faction;
import com.stud.dictionary.domain.OrderCategory;
import com.stud.dictionary.domain.Planet;
import com.stud.dictionary.domain.Sector;
import com.stud.dictionary.domain.Skill;
import com.stud.dictionary.internal.dto.DictionaryLookupDtos.CurrencyRef;
import com.stud.dictionary.internal.dto.DictionaryLookupDtos.FactionRef;
import com.stud.dictionary.internal.dto.DictionaryLookupDtos.OrderCategoryRef;
import com.stud.dictionary.internal.dto.DictionaryLookupDtos.PlanetRef;
import com.stud.dictionary.internal.dto.DictionaryLookupDtos.SectorRef;
import com.stud.dictionary.internal.dto.DictionaryLookupDtos.SkillRef;
import com.stud.dictionary.repository.CurrencyRepository;
import com.stud.dictionary.repository.FactionRepository;
import com.stud.dictionary.repository.OrderCategoryRepository;
import com.stud.dictionary.repository.PlanetRepository;
import com.stud.dictionary.repository.SectorRepository;
import com.stud.dictionary.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DictionaryLookupService {

    private final FactionRepository factionRepository;
    private final SectorRepository sectorRepository;
    private final PlanetRepository planetRepository;
    private final CurrencyRepository currencyRepository;
    private final OrderCategoryRepository orderCategoryRepository;
    private final SkillRepository skillRepository;

    public FactionRef getFaction(UUID factionId) {
        return factionRepository.findById(factionId)
                .map(this::toFactionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Faction not found: " + factionId));
    }

    public SectorRef getSector(UUID sectorId) {
        return sectorRepository.findById(sectorId)
                .map(this::toSectorRef)
                .orElseThrow(() -> new ResourceNotFoundException("Sector not found: " + sectorId));
    }

    public PlanetRef getPlanet(UUID planetId) {
        return planetRepository.findById(planetId)
                .map(this::toPlanetRef)
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found: " + planetId));
    }

    public CurrencyRef getCurrency(String currencyCode) {
        String code = normalizeCode(currencyCode);

        return currencyRepository.findById(code)
                .map(this::toCurrencyRef)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found: " + code));
    }

    public OrderCategoryRef getOrderCategory(UUID categoryId) {
        return orderCategoryRepository.findById(categoryId)
                .map(this::toOrderCategoryRef)
                .orElseThrow(() -> new ResourceNotFoundException("Order category not found: " + categoryId));
    }

    public SkillRef getSkill(UUID skillId) {
        return skillRepository.findById(skillId)
                .map(this::toSkillRef)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillId));
    }

    public List<FactionRef> getFactions(Collection<UUID> factionIds) {
        return factionRepository.findAllById(nonNullUuids(factionIds))
                .stream()
                .map(this::toFactionRef)
                .toList();
    }

    public List<SectorRef> getSectors(Collection<UUID> sectorIds) {
        return sectorRepository.findAllById(nonNullUuids(sectorIds))
                .stream()
                .map(this::toSectorRef)
                .toList();
    }

    public List<PlanetRef> getPlanets(Collection<UUID> planetIds) {
        return planetRepository.findAllById(nonNullUuids(planetIds))
                .stream()
                .map(this::toPlanetRef)
                .toList();
    }

    public List<CurrencyRef> getCurrencies(Collection<String> currencyCodes) {
        return currencyRepository.findAllById(normalizeCodes(currencyCodes))
                .stream()
                .map(this::toCurrencyRef)
                .toList();
    }

    public List<OrderCategoryRef> getOrderCategories(Collection<UUID> categoryIds) {
        return orderCategoryRepository.findAllById(nonNullUuids(categoryIds))
                .stream()
                .map(this::toOrderCategoryRef)
                .toList();
    }

    public List<SkillRef> getSkills(Collection<UUID> skillIds) {
        return skillRepository.findAllById(nonNullUuids(skillIds))
                .stream()
                .map(this::toSkillRef)
                .toList();
    }

    private List<UUID> nonNullUuids(Collection<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        return ids.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private List<String> normalizeCodes(Collection<String> codes) {
        if (codes == null || codes.isEmpty()) {
            return List.of();
        }

        return codes.stream()
                .filter(Objects::nonNull)
                .map(this::normalizeCode)
                .distinct()
                .toList();
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase();
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
