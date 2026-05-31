import { LogIn, LogOut, UserPlus } from "lucide-react";
import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { useAuth } from "../context/AuthContext.jsx";
import styles from "./AuthPanel.module.css";

const emptyForm = {
  email: "",
  username: "",
  displayName: "",
  password: "",
};

export default function AuthPanel() {
  const queryClient = useQueryClient();
  const { authError, isAuthenticated, login, logout, register, user } = useAuth();
  const [mode, setMode] = useState("login");
  const [form, setForm] = useState(emptyForm);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const updateField = (event) => {
    setForm((current) => ({
      ...current,
      [event.target.name]: event.target.value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setIsSubmitting(true);

    try {
      if (mode === "login") {
        await login({
          email: form.email,
          password: form.password,
        });
      } else {
        await register({
          email: form.email,
          username: form.username,
          displayName: form.displayName || null,
          password: form.password,
        });
      }

      setForm(emptyForm);
      queryClient.invalidateQueries();
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleLogout = async () => {
    await logout();
    queryClient.clear();
  };

  if (isAuthenticated) {
    return (
      <div className={styles.userPanel}>
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
    <form className={styles.authForm} onSubmit={handleSubmit}>
      <div className={styles.modeSwitch}>
        <button
          className={mode === "login" ? styles.active : ""}
          type="button"
          onClick={() => setMode("login")}
        >
          Вход
        </button>
        <button
          className={mode === "register" ? styles.active : ""}
          type="button"
          onClick={() => setMode("register")}
        >
          Регистрация
        </button>
      </div>
      <div className={styles.fields}>
        <input
          className="input"
          name="email"
          type="email"
          value={form.email}
          onChange={updateField}
          placeholder="email"
          required
        />
        {mode === "register" ? (
          <>
            <input
              className="input"
              name="username"
              value={form.username}
              onChange={updateField}
              placeholder="username"
              minLength={3}
              required
            />
            <input
              className="input"
              name="displayName"
              value={form.displayName}
              onChange={updateField}
              placeholder="позывной"
            />
          </>
        ) : null}
        <input
          className="input"
          name="password"
          type="password"
          value={form.password}
          onChange={updateField}
          placeholder="password"
          minLength={8}
          required
        />
      </div>
      {authError ? <div className="notice error">{authError}</div> : null}
      <button className="button" type="submit" disabled={isSubmitting}>
        {mode === "login" ? <LogIn size={17} /> : <UserPlus size={17} />}
        {mode === "login" ? "Войти" : "Создать"}
      </button>
    </form>
  );
}
