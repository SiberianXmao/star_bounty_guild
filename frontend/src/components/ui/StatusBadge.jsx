import styles from "./StatusBadge.module.css";

export default function StatusBadge({ children, tone = "neutral" }) {
  const toneClass = styles[tone] ?? styles.neutral;
  return <span className={`${styles.badge} ${toneClass}`}>{children}</span>;
}
