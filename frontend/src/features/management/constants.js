export const roleOptions = ["CLIENT", "HUNTER", "MODERATOR", "ADMIN"];

export const statusOptions = ["ACTIVE", "BLOCKED", "DELETED"];

export const moderatorStatusOptions = ["ACTIVE", "BLOCKED"];

export const factionTypes = [
  "GOVERNMENT",
  "CORPORATION",
  "GUILD",
  "PIRATES",
  "REBELS",
  "NEUTRAL",
];

export const factionRelations = ["FRIENDLY", "NEUTRAL", "HOSTILE", "ALLIED"];

export const planetStatuses = ["ACTIVE", "RESTRICTED", "UNAVAILABLE"];

export const userDefaults = {
  email: "",
  username: "",
  displayName: "",
  password: "",
  roleName: "CLIENT",
};

export const dictionaryDefaults = {
  faction: {
    name: "",
    description: "",
    type: "NEUTRAL",
    influenceLevel: 50,
    relationToGuild: "NEUTRAL",
  },
  sector: {
    name: "",
    description: "",
    controllingFactionId: "",
    stabilityLevel: 50,
    dangerLevel: 50,
  },
  planet: {
    name: "",
    description: "",
    sectorId: "",
    controllingFactionId: "",
    dangerLevel: 50,
    developmentLevel: 50,
    climate: "",
    population: "",
    status: "ACTIVE",
  },
  category: {
    name: "",
    slug: "",
    description: "",
    active: true,
  },
  currency: {
    code: "",
    name: "",
    symbol: "",
    active: true,
  },
  skill: {
    name: "",
    description: "",
  },
};
