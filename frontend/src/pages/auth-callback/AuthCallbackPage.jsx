import { useEffect, useRef, useState } from "react";
import { useQueryClient } from "@tanstack/react-query";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { LoaderCircle, ShieldCheck, TriangleAlert } from "lucide-react";
import { useAuth } from "../../context/AuthContext.jsx";
import styles from "./AuthCallbackPage.module.css";

export default function AuthCallbackPage() {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const [searchParams] = useSearchParams();
  const { completeExternalLogin } = useAuth();
  const [error, setError] = useState("");
  const didComplete = useRef(false);

  useEffect(() => {
    if (didComplete.current) {
      return;
    }

    didComplete.current = true;

    async function completeLogin() {
      try {
        const { returnTo } = await completeExternalLogin(searchParams);
        queryClient.invalidateQueries();
        navigate(returnTo || "/cabinet", { replace: true });
      } catch (loginError) {
        setError(loginError.message || "Keycloak login failed");
      }
    }

    completeLogin();
  }, [completeExternalLogin, navigate, queryClient, searchParams]);

  return (
    <section className={styles.page}>
      <article className={styles.panel}>
        {error ? (
          <>
            <span className={styles.errorIcon}>
              <TriangleAlert size={26} aria-hidden="true" />
            </span>
            <h1>Вход не завершен</h1>
            <p>{error}</p>
            <Link className="button" to="/auth?mode=login">
              Попробовать снова
            </Link>
          </>
        ) : (
          <>
            <span className={styles.statusIcon}>
              <ShieldCheck size={26} aria-hidden="true" />
            </span>
            <h1>Проверяем пропуск</h1>
            <p>Keycloak вернул код авторизации, терминал получает токены.</p>
            <LoaderCircle className={styles.spinner} size={28} aria-hidden="true" />
          </>
        )}
      </article>
    </section>
  );
}
