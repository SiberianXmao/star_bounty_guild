import { Database, UsersRound } from "lucide-react";
import styles from "../ManagementConsole.module.css";

const tabs = [
  { id: "users", label: "Пользователи", icon: UsersRound },
  { id: "dictionary", label: "Справочники", icon: Database },
];

export default function ManagementTabs({ activeTab, setActiveTab }) {
  return (
    <nav className={styles.tabs} aria-label="Разделы управления">
      {tabs.map(({ icon: Icon, id, label }) => (
        <button
          className={activeTab === id ? styles.active : ""}
          key={id}
          type="button"
          onClick={() => setActiveTab(id)}
        >
          <Icon size={17} aria-hidden="true" />
          {label}
        </button>
      ))}
    </nav>
  );
}
