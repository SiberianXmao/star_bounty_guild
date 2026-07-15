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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
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
        log.debug("Resolving internal faction reference factionId={}", factionId);

        return factionRepository.findById(factionId)
                .map(mapper::toFactionRef)
                .orElseThrow(() -> notFound("Faction", factionId));
    }

    public SectorRef getSector(UUID sectorId) {
        log.debug("Resolving internal sector reference sectorId={}", sectorId);

        return sectorRepository.findById(sectorId)
                .map(mapper::toSectorRef)
                .orElseThrow(() -> notFound("Sector", sectorId));
    }

    public PlanetRef getPlanet(UUID planetId) {
        log.debug("Resolving internal planet reference planetId={}", planetId);

        return planetRepository.findById(planetId)
                .map(mapper::toPlanetRef)
                .orElseThrow(() -> notFound("Planet", planetId));
    }

    public CurrencyRef getCurrency(String currencyCode) {
        String code = normalizeCode(currencyCode);
        log.debug("Resolving internal currency reference code='{}'", code);

        return currencyRepository.findById(code)
                .map(mapper::toCurrencyRef)
                .orElseThrow(() -> notFound("Currency", code));
    }

    public OrderCategoryRef getOrderCategory(UUID categoryId) {
        log.debug("Resolving internal order category reference categoryId={}", categoryId);

        return orderCategoryRepository.findById(categoryId)
                .map(mapper::toOrderCategoryRef)
                .orElseThrow(() -> notFound("Order category", categoryId));
    }

    public SkillRef getSkill(UUID skillId) {
        log.debug("Resolving internal skill reference skillId={}", skillId);

        return skillRepository.findById(skillId)
                .map(mapper::toSkillRef)
                .orElseThrow(() -> notFound("Skill", skillId));
    }

    public List<FactionRef> getFactions(Collection<UUID> factionIds) {
        List<UUID> ids = nonNullUuids(factionIds);
        log.debug("Resolving internal faction references requested={} normalized={}", sizeOf(factionIds), ids.size());

        List<FactionRef> refs = factionRepository.findAllById(ids)
                .stream()
                .map(mapper::toFactionRef)
                .toList();

        warnIfMissing("faction", ids.size(), refs.size());
        return refs;
    }

    public List<SectorRef> getSectors(Collection<UUID> sectorIds) {
        List<UUID> ids = nonNullUuids(sectorIds);
        log.debug("Resolving internal sector references requested={} normalized={}", sizeOf(sectorIds), ids.size());

        List<SectorRef> refs = sectorRepository.findAllById(ids)
                .stream()
                .map(mapper::toSectorRef)
                .toList();

        warnIfMissing("sector", ids.size(), refs.size());
        return refs;
    }

    public List<PlanetRef> getPlanets(Collection<UUID> planetIds) {
        List<UUID> ids = nonNullUuids(planetIds);
        log.debug("Resolving internal planet references requested={} normalized={}", sizeOf(planetIds), ids.size());

        List<PlanetRef> refs = planetRepository.findAllById(ids)
                .stream()
                .map(mapper::toPlanetRef)
                .toList();

        warnIfMissing("planet", ids.size(), refs.size());
        return refs;
    }

    public List<CurrencyRef> getCurrencies(Collection<String> currencyCodes) {
        List<String> codes = normalizeCodes(currencyCodes);
        log.debug("Resolving internal currency references requested={} normalized={}", sizeOf(currencyCodes), codes.size());

        List<CurrencyRef> refs = currencyRepository.findAllById(codes)
                .stream()
                .map(mapper::toCurrencyRef)
                .toList();

        warnIfMissing("currency", codes.size(), refs.size());
        return refs;
    }

    public List<OrderCategoryRef> getOrderCategories(Collection<UUID> categoryIds) {
        List<UUID> ids = nonNullUuids(categoryIds);
        log.debug("Resolving internal order category references requested={} normalized={}", sizeOf(categoryIds), ids.size());

        List<OrderCategoryRef> refs = orderCategoryRepository.findAllById(ids)
                .stream()
                .map(mapper::toOrderCategoryRef)
                .toList();

        warnIfMissing("order category", ids.size(), refs.size());
        return refs;
    }

    public List<SkillRef> getSkills(Collection<UUID> skillIds) {
        List<UUID> ids = nonNullUuids(skillIds);
        log.debug("Resolving internal skill references requested={} normalized={}", sizeOf(skillIds), ids.size());

        List<SkillRef> refs = skillRepository.findAllById(ids)
                .stream()
                .map(mapper::toSkillRef)
                .toList();

        warnIfMissing("skill", ids.size(), refs.size());
        return refs;
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

    private ResourceNotFoundException notFound(String entityName, Object id) {
        log.warn("Internal dictionary reference lookup failed entity={} id={}", entityName, id);
        return new ResourceNotFoundException(entityName + " not found: " + id);
    }

    private void warnIfMissing(String entityName, int requested, int found) {
        if (requested > found) {
            log.warn("Internal dictionary batch lookup returned fewer {} references than requested requested={} found={}",
                    entityName,
                    requested,
                    found
            );
        }
    }

    private int sizeOf(Collection<?> values) {
        return values == null ? 0 : values.size();
    }

}
