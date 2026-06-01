import { Link } from "react-router-dom";
import { LogIn, ShieldCheck } from "lucide-react";
import styles from "../ManagementConsole.module.css";

export default function AccessGate({ isAuthenticated, title }) {
  if (!isAuthenticated) {
    return (
      <section className={styles.gate}>
        <LogIn size={28} aria-hidden="true" />
        <h1>{title}</h1>
        <p>Доступ к служебному терминалу открыт только после входа.</p>
        <Link className="button" to="/auth?mode=login">
          <LogIn size={18} aria-hidden="true" />
          Войти
        </Link>
      </section>
    );
  }

  return (
    <section className={styles.gate}>
      <ShieldCheck size={28} aria-hidden="true" />
      <h1>Нет допуска</h1>
      <p>У текущей учетной записи нет прав на этот терминал.</p>
      <Link className="button ghost" to="/cabinet">
        В кабинет
      </Link>
    </section>
  );
}
