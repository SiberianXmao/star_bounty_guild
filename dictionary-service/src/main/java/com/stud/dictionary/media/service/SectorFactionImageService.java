package com.stud.dictionary.media.service;

import com.stud.dictionary.common.exception.ResourceNotFoundException;
import com.stud.dictionary.domain.Faction;
import com.stud.dictionary.domain.Sector;
import com.stud.dictionary.mapper.DictionaryResponseMapper;
import com.stud.dictionary.media.client.FileStorageClient;
import com.stud.dictionary.repository.FactionRepository;
import com.stud.dictionary.repository.SectorRepository;
import com.stud.dictionary.web.dto.DictionaryDtos.FactionResponse;
import com.stud.dictionary.web.dto.DictionaryDtos.SectorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.stud.dictionary.config.cache.DictionaryCacheNames.FACTIONS;
import static com.stud.dictionary.config.cache.DictionaryCacheNames.SECTORS;

@Service
@RequiredArgsConstructor
public class SectorFactionImageService {

    private final SectorRepository sectorRepository;
    private final FactionRepository factionRepository;
    private final FileStorageClient fileStorageClient;
    private final DictionaryResponseMapper responseMapper;

    @Transactional
    @CacheEvict(value = SECTORS, allEntries = true)
    public SectorResponse uploadSector(UUID sectorId, MultipartFile file) {
        Sector sector = sectorRepository.findById(sectorId)
                .orElseThrow(() -> notFound("Sector", sectorId));
        Sector saved = replace(
                "sectors",
                sectorId,
                sector,
                file,
                Sector::getImageUrl,
                Sector::setImageUrl,
                sectorRepository::save
        );
        return responseMapper.toSectorResponse(saved);
    }

    @Transactional
    @CacheEvict(value = FACTIONS, allEntries = true)
    public FactionResponse uploadFaction(UUID factionId, MultipartFile file) {
        Faction faction = factionRepository.findById(factionId)
                .orElseThrow(() -> notFound("Faction", factionId));
        Faction saved = replace(
                "factions",
                factionId,
                faction,
                file,
                Faction::getImageUrl,
                Faction::setImageUrl,
                factionRepository::save
        );
        return responseMapper.toFactionResponse(saved);
    }

    @Transactional
    @CacheEvict(value = SECTORS, allEntries = true)
    public void deleteSector(UUID sectorId) {
        Sector sector = sectorRepository.findById(sectorId)
                .orElseThrow(() -> notFound("Sector", sectorId));
        clear(sector, Sector::getImageUrl, Sector::setImageUrl, sectorRepository::save);
    }

    @Transactional
    @CacheEvict(value = FACTIONS, allEntries = true)
    public void deleteFaction(UUID factionId) {
        Faction faction = factionRepository.findById(factionId)
                .orElseThrow(() -> notFound("Faction", factionId));
        clear(faction, Faction::getImageUrl, Faction::setImageUrl, factionRepository::save);
    }

    private <T> T replace(
            String namespace,
            UUID entityId,
            T entity,
            MultipartFile file,
            Function<T, String> imageUrlGetter,
            BiConsumer<T, String> imageUrlSetter,
            Function<T, T> save
    ) {
        String previousUrl = imageUrlGetter.apply(entity);
        String newUrl = fileStorageClient.uploadImage(namespace, entityId, file);

        try {
            imageUrlSetter.accept(entity, newUrl);
            T saved = save.apply(entity);
            fileStorageClient.deleteManagedObject(previousUrl);
            return saved;
        } catch (RuntimeException exception) {
            fileStorageClient.deleteManagedObject(newUrl);
            throw exception;
        }
    }

    private <T> void clear(
            T entity,
            Function<T, String> imageUrlGetter,
            BiConsumer<T, String> imageUrlSetter,
            Function<T, T> save
    ) {
        String previousUrl = imageUrlGetter.apply(entity);
        imageUrlSetter.accept(entity, null);
        save.apply(entity);
        fileStorageClient.deleteManagedObject(previousUrl);
    }

    private ResourceNotFoundException notFound(String entityName, UUID entityId) {
        return new ResourceNotFoundException(entityName + " not found: " + entityId);
    }
}
