import { Radio } from "lucide-react";
import styles from "./EmptyState.module.css";

export default function EmptyState({ icon: Icon = Radio, title, text, action }) {
  return (
    <div className={styles.empty}>
      <span className={styles.icon}>
        <Icon size={22} aria-hidden="true" />
      </span>
      <h3>{title}</h3>
      {text ? <p>{text}</p> : null}
      {action}
    </div>
  );
}
