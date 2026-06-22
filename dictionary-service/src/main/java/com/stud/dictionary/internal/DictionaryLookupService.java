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
import com.stud.dictionary.mapper.DictionaryReferenceMapper;
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
    private final DictionaryReferenceMapper mapper;

    public FactionRef getFaction(UUID factionId) {
        return factionRepository.findById(factionId)
                .map(mapper::toFactionRef)
                .orElseThrow(() -> new ResourceNotFoundException("Faction not found: " + factionId));
    }

    public SectorRef getSector(UUID sectorId) {
        return sectorRepository.findById(sectorId)
                .map(mapper::toSectorRef)
                .orElseThrow(() -> new ResourceNotFoundException("Sector not found: " + sectorId));
    }

    public PlanetRef getPlanet(UUID planetId) {
        return planetRepository.findById(planetId)
                .map(mapper::toPlanetRef)
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found: " + planetId));
    }

    public CurrencyRef getCurrency(String currencyCode) {
        String code = normalizeCode(currencyCode);

        return currencyRepository.findById(code)
                .map(mapper::toCurrencyRef)
                .orElseThrow(() -> new ResourceNotFoundException("Currency not found: " + code));
    }

    public OrderCategoryRef getOrderCategory(UUID categoryId) {
        return orderCategoryRepository.findById(categoryId)
                .map(mapper::toOrderCategoryRef)
                .orElseThrow(() -> new ResourceNotFoundException("Order category not found: " + categoryId));
    }

    public SkillRef getSkill(UUID skillId) {
        return skillRepository.findById(skillId)
                .map(mapper::toSkillRef)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found: " + skillId));
    }

    public List<FactionRef> getFactions(Collection<UUID> factionIds) {
        return factionRepository.findAllById(nonNullUuids(factionIds))
                .stream()
                .map(mapper::toFactionRef)
                .toList();
    }

    public List<SectorRef> getSectors(Collection<UUID> sectorIds) {
        return sectorRepository.findAllById(nonNullUuids(sectorIds))
                .stream()
                .map(mapper::toSectorRef)
                .toList();
    }

    public List<PlanetRef> getPlanets(Collection<UUID> planetIds) {
        return planetRepository.findAllById(nonNullUuids(planetIds))
                .stream()
                .map(mapper::toPlanetRef)
                .toList();
    }

    public List<CurrencyRef> getCurrencies(Collection<String> currencyCodes) {
        return currencyRepository.findAllById(normalizeCodes(currencyCodes))
                .stream()
                .map(mapper::toCurrencyRef)
                .toList();
    }

    public List<OrderCategoryRef> getOrderCategories(Collection<UUID> categoryIds) {
        return orderCategoryRepository.findAllById(nonNullUuids(categoryIds))
                .stream()
                .map(mapper::toOrderCategoryRef)
                .toList();
    }

    public List<SkillRef> getSkills(Collection<UUID> skillIds) {
        return skillRepository.findAllById(nonNullUuids(skillIds))
                .stream()
                .map(mapper::toSkillRef)
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

}
