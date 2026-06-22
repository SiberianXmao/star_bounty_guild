package com.stud.profiles.dictionary;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "dictionary-service",
        url = "${app.services.dictionary.base-url}",
        path = "/internal/v1/dictionary"
)
public interface DictionaryServiceFeignClient {

    @GetMapping("/factions/{id}")
    FactionRef getFaction(@PathVariable UUID id);

    @PostMapping("/factions/lookup")
    List<FactionRef> getFactions(@RequestBody UuidLookupRequest request);

    @GetMapping("/planets/{id}")
    PlanetRef getPlanet(@PathVariable UUID id);

    @PostMapping("/planets/lookup")
    List<PlanetRef> getPlanets(@RequestBody UuidLookupRequest request);

    @GetMapping("/skills/{id}")
    SkillRef getSkill(@PathVariable UUID id);

    @PostMapping("/skills/lookup")
    List<SkillRef> getSkills(@RequestBody UuidLookupRequest request);

    record UuidLookupRequest(Collection<UUID> ids) {
    }
}
