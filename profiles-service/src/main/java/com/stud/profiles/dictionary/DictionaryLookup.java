package com.stud.profiles.dictionary;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface DictionaryLookup {

    FactionRef getFaction(UUID factionId);

    PlanetRef getPlanet(UUID planetId);

    SkillRef getSkill(UUID skillId);

    Map<UUID, FactionRef> getFactionsByIds(Collection<UUID> factionIds);

    Map<UUID, PlanetRef> getPlanetsById(Collection<UUID> planetIds);

    Map<UUID, SkillRef> getSkillsById(Collection<UUID> skillIds);
}
