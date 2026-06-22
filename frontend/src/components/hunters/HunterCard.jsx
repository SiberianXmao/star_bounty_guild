import { ArrowUpRight, BadgeCheck, Coins, MapPin, Star } from "lucide-react";
import { Link } from "react-router-dom";
import { availabilityLabel } from "../../utils/labels.js";
import { formatReward } from "../../utils/formatters.js";
import StatusBadge from "../ui/StatusBadge.jsx";
import styles from "./HunterCard.module.css";

export default function HunterCard({ hunter }) {
  const availabilityTone = hunter.availabilityStatus === "AVAILABLE" ? "calm" : "warn";

  return (
    <article className={styles.card}>
      <Link
        className={styles.cardLink}
        to={`/hunters/${hunter.id}`}
        aria-label={`Открыть досье охотника ${hunter.callsign}`}
      />
      <header className={styles.header}>
        <div className={styles.avatar}>
          {hunter.avatarUrl ? (
            <img src={hunter.avatarUrl} alt="" />
          ) : (
            hunter.callsign?.slice(0, 2).toUpperCase()
          )}
        </div>
        <div>
          <h3>{hunter.callsign}</h3>
          <p>{hunter.factionName || "Нейтральный контрактор"}</p>
        </div>
        <StatusBadge tone={availabilityTone}>{availabilityLabel(hunter.availabilityStatus)}</StatusBadge>
      </header>
      <p className={styles.bio}>{hunter.bio || "Биография скрыта в архиве гильдии."}</p>
      <div className={styles.stats}>
        <span>
          <Star size={16} aria-hidden="true" />
          {hunter.averageRating ?? "0.0"}
        </span>
        <span>
          <BadgeCheck size={16} aria-hidden="true" />
          {hunter.reliabilityScore ?? 0}
        </span>
        <span>
          <Coins size={16} aria-hidden="true" />
          {formatReward(hunter.minReward ?? 0, "CR", "CREDITS")}
        </span>
        <span>
          <MapPin size={16} aria-hidden="true" />
          {hunter.homePlanetName || "Маршрут не раскрыт"}
        </span>
      </div>
      <div className={styles.skills}>
        {(hunter.skills ?? []).slice(0, 4).map((skill) => (
          <span key={skill.skillId}>
            {skill.skillName}
            <b>{skill.level}</b>
          </span>
        ))}
      </div>
      <span className={styles.openHint}>
        Открыть досье
        <ArrowUpRight size={17} aria-hidden="true" />
      </span>
    </article>
  );
}
