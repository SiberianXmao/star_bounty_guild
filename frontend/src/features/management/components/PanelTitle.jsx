import styles from "../ManagementConsole.module.css";

export default function PanelTitle({ icon: Icon, title }) {
  return (
    <div className={styles.panelTitle}>
      <Icon size={18} aria-hidden="true" />
      <h2>{title}</h2>
    </div>
  );
}
