package com.stud.dictionary.media.web;

import com.stud.dictionary.media.service.PlanetImageService;
import com.stud.dictionary.web.dto.DictionaryDtos.PlanetResponse;
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
@RequestMapping("/api/v1/dictionary/planets/{planetId}/image")
public class PlanetImageController {

    private final PlanetImageService planetImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PlanetResponse upload(
            @PathVariable UUID planetId,
            @RequestPart("file") MultipartFile file
    ) {
        return planetImageService.upload(planetId, file);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID planetId) {
        planetImageService.delete(planetId);
    }
}
