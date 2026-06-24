import { useMemo } from "react";
import { useQuery } from "@tanstack/react-query";
import { Link, Navigate, useParams } from "react-router-dom";
import {
  ArrowLeft,
  Building2,
  Globe2,
  ImageOff,
  Map as MapIcon,
} from "lucide-react";
import EmptyState from "../../components/ui/EmptyState.jsx";
import {
  buildDirectoryPresentation,
  descriptionParagraphs,
  factionTypeLabels,
  formatPopulation,
  planetStatusLabels,
  relationLabels,
} from "../../features/directory/directoryPresentation.js";
import { dictionaryApi } from "../../services/bountyApi.js";
import { getApiErrorMessage } from "../../services/apiClient.js";
import styles from "./DirectoryDetailsPage.module.css";

const typeDefinitions = {
  factions: { label: "Фракция", listLabel: "Фракции", icon: Building2 },
  planets: { label: "Планета", listLabel: "Планеты", icon: Globe2 },
  sectors: { label: "Сектор", listLabel: "Секторы", icon: MapIcon },
};

export default function DirectoryDetailsPage() {
  const { entityId, entityType } = useParams();
  const definition = typeDefinitions[entityType];

  const dictionaryQuery = useQuery({
    queryKey: ["dictionaries"],
    queryFn: dictionaryApi.all,
    enabled: Boolean(definition),
  });

  const dictionaries = dictionaryQuery.data ?? {};
  const relations = useMemo(
    () => ({
      factions: new Map((dictionaries.factions ?? []).map((item) => [item.id, item])),
      sectors: new Map((dictionaries.sectors ?? []).map((item) => [item.id, item])),
    }),
    [dictionaries.factions, dictionaries.sectors],
  );

  if (!definition) {
    return <Navigate replace to="/directory" />;
  }

  if (dictionaryQuery.isLoading) {
    return <div className={styles.loading}>Загрузка архивной записи...</div>;
  }

  if (dictionaryQuery.isError) {
    return <div className="notice error">{getApiErrorMessage(dictionaryQuery.error)}</div>;
  }

  const item = dictionaries[entityType]?.find((entry) => entry.id === entityId);
  if (!item) {
    return (
      <div className={styles.page}>
        <BackLink definition={definition} entityType={entityType} />
        <EmptyState title="Запись не найдена" text="Возможно, объект был удален из справочника." />
      </div>
    );
  }

  const sector = item.sectorId ? relations.sectors.get(item.sectorId) : null;
  const controllingFaction = item.controllingFactionId
    ? relations.factions.get(item.controllingFactionId)
    : null;
  const presentation = buildDirectoryPresentation(entityType, item, sector, controllingFaction);
  const paragraphs = descriptionParagraphs(item.description);
  const related = buildRelatedEntities(entityType, item, dictionaries, sector, controllingFaction);
  const facts = buildDetailFacts(entityType, item, dictionaries, sector, controllingFaction);
  const EntityIcon = definition.icon;

  return (
    <div className={styles.page}>
      <BackLink definition={definition} entityType={entityType} />

      <header className={styles.titleBand}>
        <div>
          <span className={styles.eyebrow}>
            <EntityIcon size={16} aria-hidden="true" />
            Архив гильдии · {definition.label}
          </span>
          <h1>{item.name}</h1>
          {presentation.subtitle ? <p>{presentation.subtitle}</p> : null}
        </div>
        {presentation.badge ? (
          <span className={styles.badge} data-tone={presentation.badge.tone}>
            {presentation.badge.label}
          </span>
        ) : null}
      </header>

      <section className={styles.overview}>
        <div className={styles.media} data-type={entityType}>
          {item.imageUrl ? (
            <img src={item.imageUrl} alt={`${definition.label} ${item.name}`} />
          ) : (
            <div className={styles.mediaPlaceholder}>
              <EntityIcon size={52} strokeWidth={1.35} aria-hidden="true" />
              <span>Изображение не загружено</span>
              <ImageOff size={16} aria-hidden="true" />
            </div>
          )}
        </div>

        <div className={styles.characteristics}>
          <div className={styles.sectionLabel}>
            <span>Параметры записи</span>
            <strong>Характеристики</strong>
          </div>
          <div className={styles.meters}>
            {presentation.meters.map((meter) => (
              <DetailMeter key={meter.label} {...meter} />
            ))}
          </div>
          <dl className={styles.facts}>
            {facts.map(([label, value]) => (
              <div key={label}>
                <dt>{label}</dt>
                <dd>{value}</dd>
              </div>
            ))}
          </dl>
        </div>
      </section>

      <section className={styles.recordBody}>
        <article className={styles.article}>
          <div className={styles.sectionLabel}>
            <span>Полная архивная запись</span>
            <h2>Описание</h2>
          </div>
          {paragraphs.length ? (
            <div className={styles.prose}>
              {paragraphs.map((paragraph, index) => <p key={`${index}-${paragraph.slice(0, 24)}`}>{paragraph}</p>)}
            </div>
          ) : (
            <p className={styles.emptyText}>Описание пока не добавлено.</p>
          )}
        </article>

        <aside className={styles.related}>
          <div className={styles.sectionLabel}>
            <span>Навигация по архиву</span>
            <h2>Связанные записи</h2>
          </div>
          <RelatedLinks items={related} />
        </aside>
      </section>
    </div>
  );
}

function BackLink({ definition, entityType }) {
  return (
    <Link className={styles.backLink} to={`/directory?section=${entityType}`}>
      <ArrowLeft size={17} aria-hidden="true" />
      {definition.listLabel}
    </Link>
  );
}

function DetailMeter({ label, tone, value = 0 }) {
  const normalized = Math.min(100, Math.max(0, value ?? 0));
  return (
    <div className={styles.meter} data-tone={tone}>
      <div>
        <span>{label}</span>
        <strong>{normalized}</strong>
      </div>
      <span className={styles.track}><span style={{ width: `${normalized}%` }} /></span>
    </div>
  );
}

function RelatedLinks({ items }) {
  if (!items.length) return <p className={styles.emptyText}>Связанных записей нет.</p>;

  return (
    <div className={styles.relatedList}>
      {items.map((entry) => {
        const Icon = typeDefinitions[entry.type].icon;
        return (
          <Link key={`${entry.type}-${entry.item.id}`} to={`/directory/${entry.type}/${entry.item.id}`}>
            <Icon size={17} aria-hidden="true" />
            <span>
              <small>{typeDefinitions[entry.type].label}</small>
              <strong>{entry.item.name}</strong>
            </span>
          </Link>
        );
      })}
    </div>
  );
}

function buildDetailFacts(type, item, dictionaries, sector, controllingFaction) {
  if (type === "planets") {
    return [
      ["Статус", planetStatusLabels[item.status] || item.status],
      ["Климат", item.climate || "Нет данных"],
      ["Население", formatPopulation(item.population)],
      ["Сектор", sector?.name || "Не указан"],
      ["Контроль", controllingFaction?.name || "Не установлен"],
    ];
  }

  if (type === "sectors") {
    const planets = (dictionaries.planets ?? []).filter((planet) => planet.sectorId === item.id);
    return [
      ["Контроль", controllingFaction?.name || "Независимая территория"],
      ["Планет в реестре", planets.length],
    ];
  }

  const sectors = (dictionaries.sectors ?? []).filter((sectorItem) => sectorItem.controllingFactionId === item.id);
  const planets = (dictionaries.planets ?? []).filter((planet) => planet.controllingFactionId === item.id);
  return [
    ["Тип", factionTypeLabels[item.type] || item.type],
    ["Отношение", relationLabels[item.relationToGuild] || item.relationToGuild],
    ["Секторов под контролем", sectors.length],
    ["Планет под контролем", planets.length],
  ];
}

function buildRelatedEntities(type, item, dictionaries, sector, controllingFaction) {
  if (type === "planets") {
    return [
      sector ? { type: "sectors", item: sector } : null,
      controllingFaction ? { type: "factions", item: controllingFaction } : null,
    ].filter(Boolean);
  }

  if (type === "sectors") {
    return [
      controllingFaction ? { type: "factions", item: controllingFaction } : null,
      ...(dictionaries.planets ?? [])
        .filter((planet) => planet.sectorId === item.id)
        .map((planet) => ({ type: "planets", item: planet })),
    ].filter(Boolean);
  }

  return [
    ...(dictionaries.sectors ?? [])
      .filter((sectorItem) => sectorItem.controllingFactionId === item.id)
      .map((sectorItem) => ({ type: "sectors", item: sectorItem })),
    ...(dictionaries.planets ?? [])
      .filter((planet) => planet.controllingFactionId === item.id)
      .map((planet) => ({ type: "planets", item: planet })),
  ];
}
