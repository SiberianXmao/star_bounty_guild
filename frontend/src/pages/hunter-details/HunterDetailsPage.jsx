import { useQuery } from "@tanstack/react-query";
import { Link, useParams } from "react-router-dom";
import {
  AlertTriangle,
  ArrowLeft,
  BadgeCheck,
  Coins,
  Crosshair,
  MapPin,
  ShieldCheck,
  Star,
  Trophy,
} from "lucide-react";
import StatusBadge from "../../components/ui/StatusBadge.jsx";
import { profilesApi } from "../../services/bountyApi.js";
import { getApiErrorMessage } from "../../services/apiClient.js";
import { formatReward, shortId } from "../../utils/formatters.js";
import { availabilityLabel } from "../../utils/labels.js";
import styles from "./HunterDetailsPage.module.css";

function availabilityTone(status) {
  return {
    AVAILABLE: "calm",
    BUSY: "warn",
    UNAVAILABLE: "muted",
  }[status] ?? "neutral";
}

export default function HunterDetailsPage() {
  const { hunterId } = useParams();
  const hunterQuery = useQuery({
    queryKey: ["hunter", hunterId],
    queryFn: () => profilesApi.hunter(hunterId),
  });

  if (hunterQuery.isLoading) {
    return <div className={styles.loading}>Загрузка досье охотника...</div>;
  }

  if (hunterQuery.isError) {
    return (
      <div className={styles.page}>
        <Link className={styles.backLink} to="/hunters">
          <ArrowLeft size={17} aria-hidden="true" />
          Реестр охотников
        </Link>
        <div className="notice error">{getApiErrorMessage(hunterQuery.error)}</div>
      </div>
    );
  }

  const hunter = hunterQuery.data;
  const initials = hunter.callsign?.slice(0, 2).toUpperCase() || "BG";
  const successCount = hunter.completedOrdersCount ?? 0;
  const failedCount = hunter.failedOrdersCount ?? 0;
  const totalOrders = successCount + failedCount;
  const successRate = totalOrders > 0 ? Math.round((successCount / totalOrders) * 100) : 0;

  return (
    <div className={styles.page}>
      <Link className={styles.backLink} to="/hunters">
        <ArrowLeft size={17} aria-hidden="true" />
        Реестр охотников
      </Link>

      <section className={styles.identity}>
        <div className={styles.avatar}>
          {hunter.avatarUrl ? (
            <img src={hunter.avatarUrl} alt={`Аватар ${hunter.callsign}`} />
          ) : (
            initials
          )}
        </div>
        <div className={styles.identityCopy}>
          <div className={styles.eyebrow}>
            <span>Guild hunter</span>
            <span>#{shortId(hunter.id)}</span>
          </div>
          <h1>{hunter.callsign}</h1>
          <p>{hunter.bio || "Биография скрыта в архиве гильдии."}</p>
        </div>
        <StatusBadge tone={availabilityTone(hunter.availabilityStatus)}>
          {availabilityLabel(hunter.availabilityStatus)}
        </StatusBadge>
      </section>

      <section className={styles.content}>
        <div className={styles.mainColumn}>
          <article className={styles.section}>
            <header className={styles.sectionHeader}>
              <Crosshair size={20} aria-hidden="true" />
              <div>
                <span>Квалификация</span>
                <h2>Навыки охотника</h2>
              </div>
            </header>

            {(hunter.skills ?? []).length > 0 ? (
              <div className={styles.skills}>
                {hunter.skills.map((skill) => (
                  <div className={styles.skill} key={skill.skillId}>
                    <div>
                      <strong>{skill.skillName}</strong>
                      <span>{skill.level} / 100</span>
                    </div>
                    <div className={styles.skillTrack}>
                      <span style={{ width: `${Math.min(Math.max(skill.level, 0), 100)}%` }} />
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <p className={styles.emptyText}>Подтверждённые навыки пока не добавлены.</p>
            )}
          </article>

          <article className={styles.section}>
            <header className={styles.sectionHeader}>
              <ShieldCheck size={20} aria-hidden="true" />
              <div>
                <span>История операций</span>
                <h2>Репутация в гильдии</h2>
              </div>
            </header>
            <div className={styles.metrics}>
              <Metric icon={Star} label="Средний рейтинг" value={hunter.averageRating ?? "0.0"} />
              <Metric icon={BadgeCheck} label="Надёжность" value={`${hunter.reliabilityScore ?? 0}%`} />
              <Metric icon={Trophy} label="Выполнено" value={successCount} />
              <Metric icon={AlertTriangle} label="Провалено" value={failedCount} />
            </div>
            <div className={styles.successLine}>
              <span>Успешность завершённых операций</span>
              <strong>{successRate}%</strong>
            </div>
          </article>
        </div>

        <aside className={styles.sidePanel}>
          <div className={styles.dataRow}>
            <Coins size={18} aria-hidden="true" />
            <span>
              <small>Минимальная награда</small>
              <strong>{formatReward(hunter.minReward ?? 0, "CR", "CREDITS")}</strong>
            </span>
          </div>
          <div className={styles.dataRow}>
            <MapPin size={18} aria-hidden="true" />
            <span>
              <small>Домашняя планета</small>
              <strong>{hunter.homePlanetName || "Не указана"}</strong>
            </span>
          </div>
          <div className={styles.dataRow}>
            <ShieldCheck size={18} aria-hidden="true" />
            <span>
              <small>Фракция</small>
              <strong>{hunter.factionName || "Нейтральный контрактор"}</strong>
            </span>
          </div>
        </aside>
      </section>
    </div>
  );
}

function Metric({ icon: Icon, label, value }) {
  return (
    <div className={styles.metric}>
      <Icon size={18} aria-hidden="true" />
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}
