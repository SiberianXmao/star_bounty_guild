package com.stud.dictionary.catalog.images;

import com.stud.dictionary.web.dto.DictionaryDtos.FactionResponse;
import com.stud.dictionary.web.dto.DictionaryDtos.PlanetResponse;
import com.stud.dictionary.web.dto.DictionaryDtos.SectorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dictionary")
public class CatalogImageController {

    private final CatalogImageService catalogImageService;

    @PostMapping(value = "/planets/{planetId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PlanetResponse uploadPlanet(
            @PathVariable UUID planetId,
            @RequestPart("file") MultipartFile file
    ) {
        return catalogImageService.uploadPlanet(planetId, file);
    }

    @DeleteMapping("/planets/{planetId}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlanet(@PathVariable UUID planetId) {
        catalogImageService.deletePlanet(planetId);
    }

    @PostMapping(value = "/sectors/{sectorId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public SectorResponse uploadSector(
            @PathVariable UUID sectorId,
            @RequestPart("file") MultipartFile file
    ) {
        return catalogImageService.uploadSector(sectorId, file);
    }

    @DeleteMapping("/sectors/{sectorId}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSector(@PathVariable UUID sectorId) {
        catalogImageService.deleteSector(sectorId);
    }

    @PostMapping(value = "/factions/{factionId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public FactionResponse uploadFaction(
            @PathVariable UUID factionId,
            @RequestPart("file") MultipartFile file
    ) {
        return catalogImageService.uploadFaction(factionId, file);
    }

    @DeleteMapping("/factions/{factionId}/image")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFaction(@PathVariable UUID factionId) {
        catalogImageService.deleteFaction(factionId);
    }
}
