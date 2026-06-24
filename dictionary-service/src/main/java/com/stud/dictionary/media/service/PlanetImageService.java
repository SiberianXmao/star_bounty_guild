package com.stud.dictionary.media.service;

import com.stud.dictionary.common.exception.ResourceNotFoundException;
import com.stud.dictionary.domain.Planet;
import com.stud.dictionary.mapper.DictionaryResponseMapper;
import com.stud.dictionary.media.client.FileStorageClient;
import com.stud.dictionary.repository.PlanetRepository;
import com.stud.dictionary.web.dto.DictionaryDtos.PlanetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static com.stud.dictionary.config.cache.DictionaryCacheNames.PLANETS;

@Service
@RequiredArgsConstructor
public class PlanetImageService {

    private final PlanetRepository planetRepository;
    private final FileStorageClient fileStorageClient;
    private final DictionaryResponseMapper responseMapper;

    @Transactional
    @CacheEvict(value = PLANETS, allEntries = true)
    public PlanetResponse upload(UUID planetId, MultipartFile file) {
        Planet planet = findPlanet(planetId);
        String previousUrl = planet.getImageUrl();
        String newUrl = fileStorageClient.uploadImage("planets", planetId, file);

        try {
            planet.setImageUrl(newUrl);
            Planet savedPlanet = planetRepository.save(planet);
            fileStorageClient.deleteManagedObject(previousUrl);
            return responseMapper.toPlanetResponse(savedPlanet);
        } catch (RuntimeException exception) {
            fileStorageClient.deleteManagedObject(newUrl);
            throw exception;
        }
    }

    @Transactional
    @CacheEvict(value = PLANETS, allEntries = true)
    public void delete(UUID planetId) {
        Planet planet = findPlanet(planetId);
        String previousUrl = planet.getImageUrl();
        planet.setImageUrl(null);
        planetRepository.save(planet);
        fileStorageClient.deleteManagedObject(previousUrl);
    }

    private Planet findPlanet(UUID planetId) {
        return planetRepository.findById(planetId)
                .orElseThrow(() -> new ResourceNotFoundException("Planet not found: " + planetId));
    }
}
