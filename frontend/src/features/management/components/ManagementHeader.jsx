import styles from "../ManagementConsole.module.css";

export default function ManagementHeader({ dictionaries, mode, users }) {
  const isAdmin = mode === "admin";

  return (
    <section className={styles.header}>
      <div>
        <span className={styles.kicker}>{isAdmin ? "Command console" : "Moderation deck"}</span>
        <h1>{isAdmin ? "Админ-панель" : "Панель модератора"}</h1>
      </div>
      <div className={styles.headerStats}>
        <span>
          <b>{users.length}</b>
          пользователей
        </span>
        <span>
          <b>{users.filter((item) => item.status === "ACTIVE").length}</b>
          активных
        </span>
        <span>
          <b>{dictionaries.planets?.length ?? 0}</b>
          планет
        </span>
      </div>
    </section>
  );
}
