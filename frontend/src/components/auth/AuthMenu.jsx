import { LogIn, UserRound, UserPlus } from "lucide-react";
import { Link } from "react-router-dom";
import { useAuth } from "../../context/AuthContext.jsx";
import styles from "./AuthMenu.module.css";

export default function AuthMenu() {
  const { isAuthenticated, user } = useAuth();

  if (isAuthenticated) {
    return (
      <Link
        className={styles.accountButton}
        to="/cabinet"
        aria-label="Открыть личный кабинет"
        title="Личный кабинет"
      >
        {user?.avatarUrl ? (
          <img src={user.avatarUrl} alt="" />
        ) : (
          <UserRound size={21} aria-hidden="true" />
        )}
      </Link>
    );
  }

  return (
    <div className={styles.guestActions}>
      <Link className="button ghost" to="/auth?mode=login">
        <LogIn size={17} aria-hidden="true" />
        <span>Войти</span>
      </Link>
      <Link className="button" to="/auth?mode=register">
        <UserPlus size={17} aria-hidden="true" />
        <span>Регистрация</span>
      </Link>
    </div>
  );
}
