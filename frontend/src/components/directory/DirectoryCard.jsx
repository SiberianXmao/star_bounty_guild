import {
  BadgeDollarSign,
  BriefcaseBusiness,
  Building2,
  Crosshair,
  Globe2,
  Map,
  ArrowUpRight,
} from "lucide-react";
import { Link } from "react-router-dom";
import {
  buildDirectoryPresentation,
  detailedDirectoryTypes,
} from "../../features/directory/directoryPresentation.js";
import styles from "./DirectoryCard.module.css";

const iconByType = {
  categories: BriefcaseBusiness,
  currencies: BadgeDollarSign,
  factions: Building2,
  planets: Globe2,
  sectors: Map,
  skills: Crosshair,
};

export default function DirectoryCard({ item, relations, type }) {
  const Icon = iconByType[type];
  const sector = item.sectorId ? relations.sectors.get(item.sectorId) : null;
  const controllingFaction = item.controllingFactionId
    ? relations.factions.get(item.controllingFactionId)
    : null;

  const presentation = buildDirectoryPresentation(type, item, sector, controllingFaction);
  const isDetailed = detailedDirectoryTypes.has(type);

  return (
    <article className={styles.card}>
      {isDetailed ? (
        <Link
          aria-label={`Открыть подробности: ${item.name}`}
          className={styles.cardLink}
          to={`/directory/${type}/${item.id}`}
        />
      ) : null}
      {isDetailed && item.imageUrl ? (
        <img className={styles.entityImage} src={item.imageUrl} alt="" />
      ) : null}
      <header className={styles.header}>
        <span className={styles.icon}>
          <Icon size={20} aria-hidden="true" />
        </span>
        <div>
          <span className={styles.kind}>{presentation.kind}</span>
          <h3>{presentation.title}</h3>
          {presentation.subtitle ? <p>{presentation.subtitle}</p> : null}
        </div>
        {presentation.badge ? (
          <span className={styles.badge} data-tone={presentation.badge.tone}>
            {presentation.badge.label}
          </span>
        ) : null}
      </header>

      {presentation.description ? (
        <p className={styles.description}>{presentation.description}</p>
      ) : null}

      {presentation.facts.length > 0 ? (
        <dl className={styles.facts}>
          {presentation.facts.map(([label, value]) => (
            <div key={label}>
              <dt>{label}</dt>
              <dd>{value}</dd>
            </div>
          ))}
        </dl>
      ) : null}

      {presentation.meters.length > 0 ? (
        <div className={styles.meters}>
          {presentation.meters.map((meter) => (
            <Meter key={meter.label} {...meter} />
          ))}
        </div>
      ) : null}
      {isDetailed ? (
        <span className={styles.openHint}>
          Открыть досье
          <ArrowUpRight size={16} aria-hidden="true" />
        </span>
      ) : null}
    </article>
  );
}

function Meter({ label, tone, value = 0 }) {
  const normalizedValue = Math.min(100, Math.max(0, value ?? 0));

  return (
    <div className={styles.meter} data-tone={tone}>
      <div>
        <span>{label}</span>
        <b>{normalizedValue}</b>
      </div>
      <span className={styles.track}>
        <span style={{ width: `${normalizedValue}%` }} />
      </span>
    </div>
  );
}
