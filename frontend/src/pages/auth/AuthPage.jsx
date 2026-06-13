import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { Badge, KeyRound, LogIn, Mail, ShieldCheck, UserPlus } from "lucide-react";
import { useAuth } from "../../context/AuthContext.jsx";
import heroImage from "../../assets/guild-operations-deck.png";
import styles from "./AuthPage.module.css";

const emptyForm = {
  email: "",
  username: "",
  displayName: "",
  password: "",
};

export default function AuthPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [searchParams, setSearchParams] = useSearchParams();
  const { authError, isAuthenticated, isKeycloakAuth, login, register, user } = useAuth();
  const [form, setForm] = useState(emptyForm);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const mode = searchParams.get("mode") === "register" ? "register" : "login";

  const setMode = (nextMode) => {
    setSearchParams({ mode: nextMode });
  };

  const updateField = (event) => {
    const { name, value } = event.target;
    setForm((current) => ({
      ...current,
      [name]: value,
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
      navigate("/cabinet");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleKeycloakAuth = async () => {
    setIsSubmitting(true);

    try {
      if (mode === "login") {
        await login({});
      } else {
        await register({});
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className={styles.page}>
      <section
        className={styles.authShell}
        style={{
          backgroundImage: `linear-gradient(100deg, rgba(15, 13, 11, 0.96), rgba(15, 13, 11, 0.78) 44%, rgba(15, 13, 11, 0.28)), url(${heroImage})`,
        }}
      >
        <div className={styles.copy}>
          <span className={styles.kicker}>Secure terminal</span>
          <h1>Доступ к гильдии</h1>
          <div className={styles.signalGrid}>
            <span>
              <b>{isKeycloakAuth ? "OIDC" : "JWT"}</b>
              access
            </span>
            <span>
              <b>RBAC</b>
              roles
            </span>
            <span>
              <b>PKCE</b>
              flow
            </span>
          </div>
        </div>

        <article className={styles.card}>
          {isAuthenticated ? (
            <div className={styles.signedIn}>
              <span className={styles.statusIcon}>
                <ShieldCheck size={26} aria-hidden="true" />
              </span>
              <h2>{user?.displayName || user?.username}</h2>
              <p>{user?.email}</p>
              <Link className="button" to="/cabinet">
                В кабинет
              </Link>
            </div>
          ) : (
            <>
              <div className={styles.cardHeader}>
                <span className={styles.statusIcon}>
                  {isKeycloakAuth ? (
                    <ShieldCheck size={24} aria-hidden="true" />
                  ) : mode === "login" ? (
                    <LogIn size={24} aria-hidden="true" />
                  ) : (
                    <UserPlus size={24} aria-hidden="true" />
                  )}
                </span>
                <div>
                  <h2>{mode === "login" ? "Вход" : "Регистрация"}</h2>
                  <p>
                    {isKeycloakAuth
                      ? "Keycloak проверит личность и вернет токены"
                      : mode === "login"
                        ? "Продолжить с аккаунтом"
                        : "Создать аккаунт заказчика"}
                  </p>
                </div>
              </div>

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

              {isKeycloakAuth ? (
                <KeycloakPanel
                  authError={authError}
                  isSubmitting={isSubmitting}
                  mode={mode}
                  onSubmit={handleKeycloakAuth}
                />
              ) : (
                <LocalAuthForm
                  authError={authError}
                  form={form}
                  isSubmitting={isSubmitting}
                  mode={mode}
                  onChange={updateField}
                  onSubmit={handleSubmit}
                />
              )}
            </>
          )}
        </article>
      </section>
    </div>
  );
}

function KeycloakPanel({ authError, isSubmitting, mode, onSubmit }) {
  return (
    <div className={styles.providerPanel}>
      <div className={styles.providerMeta}>
        <span>Identity Provider</span>
        <strong>Keycloak / bounty-guild</strong>
        <p>
          После входа frontend получит access token, а user-service сверит роли через
          OAuth2 Resource Server.
        </p>
      </div>

      {authError ? <div className="notice error">{authError}</div> : null}

      <button className="button" type="button" onClick={onSubmit} disabled={isSubmitting}>
        {mode === "login" ? (
          <LogIn size={18} aria-hidden="true" />
        ) : (
          <UserPlus size={18} aria-hidden="true" />
        )}
        {mode === "login" ? "Войти через Keycloak" : "Создать аккаунт в Keycloak"}
      </button>
    </div>
  );
}

function LocalAuthForm({ authError, form, isSubmitting, mode, onChange, onSubmit }) {
  return (
    <form className={styles.form} onSubmit={onSubmit}>
      <AuthField
        icon={Mail}
        label="Email"
        name="email"
        onChange={onChange}
        placeholder="hunter@bounty.local"
        type="email"
        value={form.email}
      />

      {mode === "register" ? (
        <>
          <AuthField
            icon={Badge}
            label="Username"
            minLength={3}
            name="username"
            onChange={onChange}
            placeholder="callsign"
            value={form.username}
          />
          <AuthField
            icon={Badge}
            label="Имя в терминале"
            name="displayName"
            onChange={onChange}
            placeholder="Outer Rim Operator"
            required={false}
            value={form.displayName}
          />
        </>
      ) : null}

      <AuthField
        icon={KeyRound}
        label="Пароль"
        minLength={8}
        name="password"
        onChange={onChange}
        placeholder="минимум 8 символов"
        type="password"
        value={form.password}
      />

      {authError ? <div className="notice error">{authError}</div> : null}

      <button className="button" type="submit" disabled={isSubmitting}>
        {mode === "login" ? (
          <LogIn size={18} aria-hidden="true" />
        ) : (
          <UserPlus size={18} aria-hidden="true" />
        )}
        {mode === "login" ? "Войти" : "Создать аккаунт"}
      </button>
    </form>
  );
}

function AuthField({
  icon: Icon,
  label,
  name,
  onChange,
  placeholder,
  required = true,
  type = "text",
  value,
  ...props
}) {
  return (
    <label className={styles.field}>
      <span>{label}</span>
      <div className={styles.control}>
        <Icon size={17} aria-hidden="true" />
        <input
          name={name}
          onChange={onChange}
          placeholder={placeholder}
          required={required}
          type={type}
          value={value}
          {...props}
        />
      </div>
    </label>
  );
}
