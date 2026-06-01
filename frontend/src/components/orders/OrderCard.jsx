import { CalendarClock, Coins, MapPin, Star } from "lucide-react";
import { Link } from "react-router-dom";
import {
  acceptanceLabel,
  riskLabel,
  statusLabel,
  toneForRisk,
  toneForStatus,
  urgencyLabel,
} from "../../utils/labels.js";
import { formatDate, formatReward, shortId } from "../../utils/formatters.js";
import StatusBadge from "../ui/StatusBadge.jsx";
import styles from "./OrderCard.module.css";

export default function OrderCard({ order }) {
  return (
    <article className={styles.card}>
      <div className={styles.cardTop}>
        <div className={styles.id}>#{shortId(order.id)}</div>
        <div className={styles.badges}>
          <StatusBadge tone={toneForStatus(order.status)}>{statusLabel(order.status)}</StatusBadge>
          <StatusBadge tone={toneForRisk(order.riskLevel)}>{riskLabel(order.riskLevel)}</StatusBadge>
        </div>
      </div>
      <h3>
        <Link to={`/orders/${order.id}`}>{order.title}</Link>
      </h3>
      <p className={styles.description}>{order.description}</p>
      <div className={styles.metaGrid}>
        <span>
          <Coins size={16} aria-hidden="true" />
          {formatReward(order.rewardAmount, order.rewardCurrencySymbol, order.rewardCurrencyCode)}
        </span>
        <span>
          <MapPin size={16} aria-hidden="true" />
          {order.planetName || order.sectorName || "Координаты скрыты"}
        </span>
        <span>
          <CalendarClock size={16} aria-hidden="true" />
          {formatDate(order.deadline)}
        </span>
        <span>
          <Star size={16} aria-hidden="true" />
          {order.clientAverageRating ?? "0.0"} / {order.clientReliabilityScore ?? 0}
        </span>
      </div>
      <footer className={styles.footer}>
        <span>{order.categoryName}</span>
        <span>
          {urgencyLabel(order.urgencyLevel)} · {acceptanceLabel(order.acceptanceMode)}
        </span>
      </footer>
    </article>
  );
}
