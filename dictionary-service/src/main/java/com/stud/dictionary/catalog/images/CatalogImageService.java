package com.stud.dictionary.catalog.images;

import com.stud.dictionary.common.exception.ResourceNotFoundException;
import com.stud.dictionary.domain.Faction;
import com.stud.dictionary.domain.Planet;
import com.stud.dictionary.domain.Sector;
import com.stud.dictionary.integrations.files.FileStorageClient;
import com.stud.dictionary.mapper.DictionaryResponseMapper;
import com.stud.dictionary.repository.FactionRepository;
import com.stud.dictionary.repository.PlanetRepository;
import com.stud.dictionary.repository.SectorRepository;
import com.stud.dictionary.web.dto.DictionaryDtos.FactionResponse;
import com.stud.dictionary.web.dto.DictionaryDtos.PlanetResponse;
import com.stud.dictionary.web.dto.DictionaryDtos.SectorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.stud.dictionary.config.cache.DictionaryCacheNames.FACTIONS;
import static com.stud.dictionary.config.cache.DictionaryCacheNames.PLANETS;
import static com.stud.dictionary.config.cache.DictionaryCacheNames.SECTORS;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogImageService {

    private final PlanetRepository planetRepository;
    private final SectorRepository sectorRepository;
    private final FactionRepository factionRepository;
    private final FileStorageClient fileStorageClient;
    private final DictionaryResponseMapper responseMapper;

    @Transactional
    @CacheEvict(value = PLANETS, allEntries = true)
    public PlanetResponse uploadPlanet(UUID planetId, MultipartFile file) {
        log.info("Uploading planet image planetId={} originalFilename='{}' size={}", planetId, file.getOriginalFilename(), file.getSize());

        Planet planet = planetRepository.findById(planetId)
                .orElseThrow(() -> notFound("Planet", planetId));
        Planet saved = replaceImage(
                "planets",
                planetId,
                planet,
                file,
                Planet::getImageUrl,
                Planet::setImageUrl,
                planetRepository::save
        );
        log.info("Uploaded planet image planetId={} imageUrl='{}'", saved.getId(), saved.getImageUrl());
        return responseMapper.toPlanetResponse(saved);
    }

    @Transactional
    @CacheEvict(value = PLANETS, allEntries = true)
    public void deletePlanet(UUID planetId) {
        log.info("Deleting planet image planetId={}", planetId);

        Planet planet = planetRepository.findById(planetId)
                .orElseThrow(() -> notFound("Planet", planetId));
        clearImage(planet, Planet::getImageUrl, Planet::setImageUrl, planetRepository::save);
        log.info("Deleted planet image planetId={}", planetId);
    }

    @Transactional
    @CacheEvict(value = SECTORS, allEntries = true)
    public SectorResponse uploadSector(UUID sectorId, MultipartFile file) {
        log.info("Uploading sector image sectorId={} originalFilename='{}' size={}", sectorId, file.getOriginalFilename(), file.getSize());

        Sector sector = sectorRepository.findById(sectorId)
                .orElseThrow(() -> notFound("Sector", sectorId));
        Sector saved = replaceImage(
                "sectors",
                sectorId,
                sector,
                file,
                Sector::getImageUrl,
                Sector::setImageUrl,
                sectorRepository::save
        );
        log.info("Uploaded sector image sectorId={} imageUrl='{}'", saved.getId(), saved.getImageUrl());
        return responseMapper.toSectorResponse(saved);
    }

    @Transactional
    @CacheEvict(value = SECTORS, allEntries = true)
    public void deleteSector(UUID sectorId) {
        log.info("Deleting sector image sectorId={}", sectorId);

        Sector sector = sectorRepository.findById(sectorId)
                .orElseThrow(() -> notFound("Sector", sectorId));
        clearImage(sector, Sector::getImageUrl, Sector::setImageUrl, sectorRepository::save);
        log.info("Deleted sector image sectorId={}", sectorId);
    }

    @Transactional
    @CacheEvict(value = FACTIONS, allEntries = true)
    public FactionResponse uploadFaction(UUID factionId, MultipartFile file) {
        log.info("Uploading faction image factionId={} originalFilename='{}' size={}", factionId, file.getOriginalFilename(), file.getSize());

        Faction faction = factionRepository.findById(factionId)
                .orElseThrow(() -> notFound("Faction", factionId));
        Faction saved = replaceImage(
                "factions",
                factionId,
                faction,
                file,
                Faction::getImageUrl,
                Faction::setImageUrl,
                factionRepository::save
        );
        log.info("Uploaded faction image factionId={} imageUrl='{}'", saved.getId(), saved.getImageUrl());
        return responseMapper.toFactionResponse(saved);
    }

    @Transactional
    @CacheEvict(value = FACTIONS, allEntries = true)
    public void deleteFaction(UUID factionId) {
        log.info("Deleting faction image factionId={}", factionId);

        Faction faction = factionRepository.findById(factionId)
                .orElseThrow(() -> notFound("Faction", factionId));
        clearImage(faction, Faction::getImageUrl, Faction::setImageUrl, factionRepository::save);
        log.info("Deleted faction image factionId={}", factionId);
    }

    private <T> T replaceImage(
            String category,
            UUID entityId,
            T entity,
            MultipartFile file,
            Function<T, String> imageUrlGetter,
            BiConsumer<T, String> imageUrlSetter,
            Function<T, T> save
    ) {
        String previousUrl = imageUrlGetter.apply(entity);
        log.debug("Replacing catalog image category={} entityId={} previousUrl='{}'", category, entityId, previousUrl);

        String newUrl = fileStorageClient.uploadImage(category, entityId, file);

        try {
            imageUrlSetter.accept(entity, newUrl);
            T saved = save.apply(entity);
            fileStorageClient.deleteManagedObject(previousUrl);
            log.debug("Replaced catalog image category={} entityId={} newUrl='{}'", category, entityId, newUrl);
            return saved;
        } catch (RuntimeException exception) {
            log.error("Failed to persist catalog image replacement category={} entityId={} newUrl='{}'", category, entityId, newUrl, exception);
            fileStorageClient.deleteManagedObject(newUrl);
            throw exception;
        }
    }

    private <T> void clearImage(
            T entity,
            Function<T, String> imageUrlGetter,
            BiConsumer<T, String> imageUrlSetter,
            Function<T, T> save
    ) {
        String previousUrl = imageUrlGetter.apply(entity);

        if (previousUrl == null || previousUrl.isBlank()) {
            log.warn("Catalog image delete requested but entity has no image");
        }

        imageUrlSetter.accept(entity, null);
        save.apply(entity);
        fileStorageClient.deleteManagedObject(previousUrl);
    }

    private ResourceNotFoundException notFound(String entityName, UUID entityId) {
        log.warn("{} image operation failed because entity was not found id={}", entityName, entityId);
        return new ResourceNotFoundException(entityName + " not found: " + entityId);
    }
}
