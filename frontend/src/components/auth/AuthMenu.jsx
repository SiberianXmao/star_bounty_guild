import { LogIn, LogOut, UserPlus } from "lucide-react";
import { useQueryClient } from "@tanstack/react-query";
import { Link } from "react-router-dom";
import { useAuth } from "../../context/AuthContext.jsx";
import styles from "./AuthMenu.module.css";

export default function AuthMenu() {
  const queryClient = useQueryClient();
  const { isAuthenticated, logout, user } = useAuth();

  const handleLogout = async () => {
    await logout();
    queryClient.clear();
  };

  if (isAuthenticated) {
    return (
      <div className={styles.userPanel}>
        <Link className={styles.avatar} to="/cabinet" aria-label="Открыть кабинет">
          {user?.avatarUrl ? (
            <img src={user.avatarUrl} alt="" />
          ) : (
            <span>{(user?.displayName || user?.username || "BG").slice(0, 2).toUpperCase()}</span>
          )}
        </Link>
        <div>
          <span className={styles.userName}>
            {user?.displayName || user?.username || user?.email}
          </span>
          <span className={styles.roles}>{user?.roles?.join(" / ")}</span>
        </div>
        <button className="iconButton" type="button" onClick={handleLogout} aria-label="Выйти">
          <LogOut size={18} aria-hidden="true" />
        </button>
      </div>
    );
  }

  return (
    <div className={styles.guestActions}>
      <Link className="button ghost" to="/auth?mode=login">
        <LogIn size={17} aria-hidden="true" />
        Войти
      </Link>
      <Link className="button" to="/auth?mode=register">
        <UserPlus size={17} aria-hidden="true" />
        Регистрация
      </Link>
    </div>
  );
}
