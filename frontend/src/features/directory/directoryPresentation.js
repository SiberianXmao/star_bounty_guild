export const detailedDirectoryTypes = new Set(["planets", "sectors", "factions"]);

export const factionTypeLabels = {
  CORPORATION: "Корпорация",
  GOVERNMENT: "Правительство",
  GUILD: "Гильдия",
  NEUTRAL: "Независимые",
  PIRATES: "Синдикат",
  REBELS: "Повстанцы",
};

export const relationLabels = {
  ALLIED: "Союзники",
  FRIENDLY: "Дружественные",
  HOSTILE: "Враждебные",
  NEUTRAL: "Нейтральные",
};

export const planetStatusLabels = {
  ACTIVE: "Доступна",
  RESTRICTED: "Ограниченный доступ",
  UNAVAILABLE: "Недоступна",
};

export function formatPopulation(value) {
  if (value === null || value === undefined) return "Нет данных";
  if (value === 0) return "Нет постоянного населения";

  return new Intl.NumberFormat("ru-RU", {
    notation: "compact",
    maximumFractionDigits: 1,
  }).format(value);
}

export function descriptionSummary(description, maxLength = 230) {
  const firstParagraph = description?.split(/\n\s*\n/)[0]?.trim();
  if (!firstParagraph || firstParagraph.length <= maxLength) return firstParagraph;
  return `${firstParagraph.slice(0, maxLength).trimEnd()}...`;
}

export function descriptionParagraphs(description) {
  return description?.split(/\n\s*\n/).map((paragraph) => paragraph.trim()).filter(Boolean) ?? [];
}

export function toneForRelation(relation) {
  if (relation === "HOSTILE") return "danger";
  if (relation === "ALLIED" || relation === "FRIENDLY") return "positive";
  return "neutral";
}

export function toneForPlanetStatus(status) {
  if (status === "UNAVAILABLE") return "danger";
  if (status === "RESTRICTED") return "warning";
  return "positive";
}

export function relationScore(relation) {
  return { ALLIED: 100, FRIENDLY: 75, NEUTRAL: 50, HOSTILE: 15 }[relation] ?? 0;
}

export function buildDirectoryPresentation(type, item, sector, controllingFaction) {
  if (type === "planets") {
    return {
      kind: "Планета",
      title: item.name,
      subtitle: sector?.name || "Сектор не указан",
      description: descriptionSummary(item.description),
      badge: {
        label: planetStatusLabels[item.status] || item.status,
        tone: toneForPlanetStatus(item.status),
      },
      facts: [
        ["Климат", item.climate || "Нет данных"],
        ["Население", formatPopulation(item.population)],
        ["Контроль", controllingFaction?.name || "Не установлен"],
      ],
      meters: [
        { label: "Опасность", value: item.dangerLevel, tone: "danger" },
        { label: "Развитие", value: item.developmentLevel, tone: "positive" },
      ],
    };
  }

  if (type === "sectors") {
    return {
      kind: "Сектор",
      title: item.name,
      subtitle: controllingFaction?.name || "Независимая территория",
      description: descriptionSummary(item.description),
      badge: null,
      facts: [],
      meters: [
        { label: "Стабильность", value: item.stabilityLevel, tone: "positive" },
        { label: "Опасность", value: item.dangerLevel, tone: "danger" },
      ],
    };
  }

  if (type === "factions") {
    return {
      kind: factionTypeLabels[item.type] || "Фракция",
      title: item.name,
      subtitle: null,
      description: descriptionSummary(item.description),
      badge: {
        label: relationLabels[item.relationToGuild] || item.relationToGuild,
        tone: toneForRelation(item.relationToGuild),
      },
      facts: [],
      meters: [
        { label: "Влияние", value: item.influenceLevel, tone: "accent" },
        {
          label: "Доверие гильдии",
          value: relationScore(item.relationToGuild),
          tone: item.relationToGuild === "HOSTILE" ? "danger" : "positive",
        },
      ],
    };
  }

  if (type === "categories") {
    return {
      kind: "Тип контракта",
      title: item.name,
      subtitle: item.slug,
      description: descriptionSummary(item.description),
      badge: { label: item.active ? "Доступен" : "Архив", tone: item.active ? "positive" : "neutral" },
      facts: [],
      meters: [],
    };
  }

  if (type === "currencies") {
    return {
      kind: "Расчетная единица",
      title: item.name,
      subtitle: item.code,
      description: null,
      badge: {
        label: item.active ? "В обращении" : "Не используется",
        tone: item.active ? "positive" : "neutral",
      },
      facts: [["Обозначение", item.symbol || item.code]],
      meters: [],
    };
  }

  return {
    kind: "Навык",
    title: item.name,
    subtitle: null,
    description: descriptionSummary(item.description),
    badge: null,
    facts: [],
    meters: [],
  };
}
