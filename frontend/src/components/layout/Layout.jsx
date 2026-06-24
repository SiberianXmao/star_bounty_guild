import { Link, NavLink } from "react-router-dom";
import { BookOpen, Crosshair, LayoutDashboard, ShieldCheck } from "lucide-react";
import { useAuth } from "../../context/AuthContext.jsx";
import AuthMenu from "../auth/AuthMenu.jsx";
import styles from "./Layout.module.css";

const navItems = [
  { to: "/", label: "Контракты", icon: LayoutDashboard },
  { to: "/hunters", label: "Охотники", icon: Crosshair },
  { to: "/directory", label: "Справочник", icon: BookOpen },
];

export default function Layout({ children }) {
  const { user } = useAuth();
  const userRoles = user?.roles ?? [];
  const visibleNavItems = userRoles.includes("ADMIN")
    ? [...navItems, { to: "/admin", label: "Админ", icon: ShieldCheck }]
    : userRoles.includes("MODERATOR")
      ? [...navItems, { to: "/moderation", label: "Модерация", icon: ShieldCheck }]
      : navItems;

  return (
    <div className={styles.shell}>
      <header className={styles.header}>
        <Link className={styles.brand} to="/">
          <span className={styles.brandMark}>BG</span>
          <span>
            <strong>Bounty Guild</strong>
            <small>contract board</small>
          </span>
        </Link>
        <nav className={styles.nav} aria-label="Основная навигация">
          {visibleNavItems.map(({ icon: Icon, label, to }) => (
            <NavLink
              className={({ isActive }) => `${styles.navLink} ${isActive ? styles.active : ""}`}
              key={to}
              to={to}
            >
              <Icon size={17} aria-hidden="true" />
              {label}
            </NavLink>
          ))}
        </nav>
        <div className={styles.auth}>
          <AuthMenu />
        </div>
      </header>
      <main className={styles.main}>{children}</main>
    </div>
  );
}
