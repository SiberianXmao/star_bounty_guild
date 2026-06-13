import { useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { Badge, KeyRound, LogIn, Mail, RadioTower, ShieldCheck, UserPlus } from "lucide-react";
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
  const { authError, isAuthenticated, login, register, user } = useAuth();
  const [form, setForm] = useState(emptyForm);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const mode = searchParams.get("mode") === "register" ? "register" : "login";
  const isRegister = mode === "register";

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
      if (isRegister) {
        await register({
          email: form.email,
          username: form.username,
          displayName: form.displayName || null,
          password: form.password,
        });
      } else {
        await login({
          email: form.email,
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

  return (
    <div className={styles.page}>
      <section
        className={styles.authShell}
        style={{
          backgroundImage: `linear-gradient(100deg, rgba(15, 13, 11, 0.96), rgba(15, 13, 11, 0.78) 44%, rgba(15, 13, 11, 0.28)), url(${heroImage})`,
        }}
      >
        <div className={styles.copy}>
          <span className={styles.kicker}>Канал гильдии</span>
          <h1>Доступ к охотникам</h1>
          <div className={styles.signalGrid} aria-label="Разделы кабинета">
            <span>
              <b>Контракты</b>
              доска
            </span>
            <span>
              <b>Профиль</b>
              кабинет
            </span>
            <span>
              <b>Роли</b>
              доступ
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
                  {isRegister ? <UserPlus size={24} aria-hidden="true" /> : <LogIn size={24} aria-hidden="true" />}
                </span>
                <div>
                  <h2>{isRegister ? "Регистрация" : "Вход"}</h2>
                  <p>
                    {isRegister
                      ? "Создай профиль и открой личный кабинет гильдии."
                      : "Вернись к контрактам, профилю и откликам."}
                  </p>
                </div>
              </div>

              <div className={styles.modeSwitch} aria-label="Режим авторизации">
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

              <AuthForm
                authError={authError}
                form={form}
                isRegister={isRegister}
                isSubmitting={isSubmitting}
                onChange={updateField}
                onSubmit={handleSubmit}
              />
            </>
          )}
        </article>
      </section>
    </div>
  );
}

function AuthForm({ authError, form, isRegister, isSubmitting, onChange, onSubmit }) {
  return (
    <form className={styles.form} onSubmit={onSubmit}>
      <AuthField
        autoComplete="email"
        icon={Mail}
        label="Email"
        name="email"
        onChange={onChange}
        placeholder="hunter@bounty.local"
        type="email"
        value={form.email}
      />

      {isRegister ? (
        <div className={styles.formGrid}>
          <AuthField
            autoComplete="username"
            icon={Badge}
            label="Позывной"
            minLength={3}
            name="username"
            onChange={onChange}
            placeholder="outer-rim"
            value={form.username}
          />
          <AuthField
            autoComplete="name"
            icon={RadioTower}
            label="Имя в гильдии"
            name="displayName"
            onChange={onChange}
            placeholder="Outer Rim Operator"
            required={false}
            value={form.displayName}
          />
        </div>
      ) : null}

      <AuthField
        autoComplete={isRegister ? "new-password" : "current-password"}
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

      <button className={`button ${styles.submitButton}`} type="submit" disabled={isSubmitting}>
        {isRegister ? <UserPlus size={18} aria-hidden="true" /> : <LogIn size={18} aria-hidden="true" />}
        {isSubmitting ? "Проверка..." : isRegister ? "Создать аккаунт" : "Войти"}
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
