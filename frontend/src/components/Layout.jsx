import { Link, NavLink } from "react-router-dom";
import { Compass, Crosshair, LayoutDashboard } from "lucide-react";
import AuthPanel from "./AuthPanel.jsx";
import styles from "./Layout.module.css";

const navItems = [
  { to: "/", label: "Контракты", icon: LayoutDashboard },
  { to: "/hunters", label: "Охотники", icon: Crosshair },
  { to: "/cabinet", label: "Кабинет", icon: Compass },
];

export default function Layout({ children }) {
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
          {navItems.map(({ icon: Icon, label, to }) => (
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
          <AuthPanel />
        </div>
      </header>
      <main className={styles.main}>{children}</main>
    </div>
  );
}
