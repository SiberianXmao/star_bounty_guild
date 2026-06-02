import { normalizeOptionalString } from "../../utils/formatters.js";

export function updateDictionaryForm(setForms, type, updater) {
  setForms((current) => ({
    ...current,
    [type]: typeof updater === "function" ? updater(current[type]) : updater,
  }));
}

export function updateFormValue(setForm, event) {
  const { name, value } = event.target;
  setForm((current) => ({
    ...current,
    [name]: value,
  }));
}

export function normalizeDictionaryPayload(type, form) {
  if (type === "faction") {
    return {
      ...form,
      description: normalizeOptionalString(form.description),
      influenceLevel: Number(form.influenceLevel),
    };
  }

  if (type === "sector") {
    return {
      ...form,
      controllingFactionId: form.controllingFactionId || null,
      description: normalizeOptionalString(form.description),
      dangerLevel: Number(form.dangerLevel),
      stabilityLevel: Number(form.stabilityLevel),
    };
  }

  if (type === "planet") {
    return {
      ...form,
      climate: normalizeOptionalString(form.climate),
      controllingFactionId: form.controllingFactionId || null,
      description: normalizeOptionalString(form.description),
      developmentLevel: Number(form.developmentLevel),
      dangerLevel: Number(form.dangerLevel),
      population: form.population ? Number(form.population) : null,
    };
  }

  if (type === "category") {
    return {
      ...form,
      description: normalizeOptionalString(form.description),
      slug: form.slug.trim().toLowerCase(),
    };
  }

  if (type === "currency") {
    return {
      ...form,
      code: form.code.trim().toUpperCase(),
      symbol: normalizeOptionalString(form.symbol),
    };
  }

  return {
    ...form,
    description: normalizeOptionalString(form.description),
  };
}

export function buildCatalogModel(dictionaries) {
  const factions = dictionaries.factions ?? [];
  const sectors = dictionaries.sectors ?? [];
  const planets = dictionaries.planets ?? [];
  const factionById = new Map(factions.map((faction) => [faction.id, faction]));
  const sectorById = new Map(sectors.map((sector) => [sector.id, sector]));

  const planetsBySectorId = planets.reduce((acc, planet) => {
    const key = planet.sectorId ?? "unknown";
    acc.set(key, [...(acc.get(key) ?? []), planet]);
    return acc;
  }, new Map());

  const sectorsByFactionId = sectors.reduce((acc, sector) => {
    if (!sector.controllingFactionId) {
      return acc;
    }

    acc.set(sector.controllingFactionId, [
      ...(acc.get(sector.controllingFactionId) ?? []),
      sector,
    ]);
    return acc;
  }, new Map());

  const planetsByFactionId = planets.reduce((acc, planet) => {
    if (!planet.controllingFactionId) {
      return acc;
    }

    acc.set(planet.controllingFactionId, [
      ...(acc.get(planet.controllingFactionId) ?? []),
      planet,
    ]);
    return acc;
  }, new Map());

  return {
    factionById,
    factions: factions.map((faction) => ({
      ...faction,
      controlledPlanets: planetsByFactionId.get(faction.id) ?? [],
      controlledSectors: sectorsByFactionId.get(faction.id) ?? [],
    })),
    planets: planets.map((planet) => ({
      ...planet,
      controllingFaction: factionById.get(planet.controllingFactionId),
      sector: sectorById.get(planet.sectorId),
    })),
    sectors: sectors.map((sector) => ({
      ...sector,
      controllingFaction: factionById.get(sector.controllingFactionId),
      planets: planetsBySectorId.get(sector.id) ?? [],
    })),
    sectorById,
  };
}

export function matchesCatalogSearch(item, search, fields) {
  if (!search) {
    return true;
  }

  return fields.some((field) => item[field]?.toLowerCase().includes(search));
}

export function isStaffUser(user) {
  return user.roles?.includes("ADMIN") || user.roles?.includes("MODERATOR");
}

export function uniqueRolesFromUsers(users) {
  const roles = new Set();

  users.forEach((user) => {
    user.roles?.forEach((role) => roles.add(role));
  });

  return [...roles].sort();
}
