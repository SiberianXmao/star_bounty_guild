import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { AUTH_PROVIDER, isKeycloakAuthEnabled } from "../config/auth.js";
import { authApi } from "../services/bountyApi.js";
import {
  clearStoredSession,
  getApiErrorMessage,
  getStoredSession,
  saveStoredSession,
} from "../services/apiClient.js";
import {
  buildKeycloakLogoutUrl,
  completeKeycloakLogin,
  startKeycloakLogin,
} from "../services/oidcClient.js";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [session, setSession] = useState(() => getStoredSession());
  const [isBooting, setIsBooting] = useState(Boolean(session?.accessToken));
  const [authError, setAuthError] = useState("");

  useEffect(() => {
    const syncSession = () => setSession(getStoredSession());

    window.addEventListener("storage", syncSession);
    window.addEventListener("bountyGuild:session", syncSession);

    return () => {
      window.removeEventListener("storage", syncSession);
      window.removeEventListener("bountyGuild:session", syncSession);
    };
  }, []);

  useEffect(() => {
    let isMounted = true;

    async function loadMe() {
      if (!session?.accessToken) {
        setIsBooting(false);
        return;
      }

      try {
        const user = await authApi.me();
        const nextSession = { ...getStoredSession(), user };
        saveStoredSession(nextSession);

        if (isMounted) {
          setSession(nextSession);
        }
      } catch {
        clearStoredSession();
      } finally {
        if (isMounted) {
          setIsBooting(false);
        }
      }
    }

    loadMe();

    return () => {
      isMounted = false;
    };
  }, []);

  const login = useCallback(async (payload) => {
    setAuthError("");

    if (isKeycloakAuthEnabled()) {
      await startKeycloakLogin({ mode: "login" });
      return null;
    }

    try {
      const nextSession = await authApi.login(payload);
      setSession(nextSession);
      return nextSession;
    } catch (error) {
      const message = getApiErrorMessage(error);
      setAuthError(message);
      throw error;
    }
  }, []);

  const register = useCallback(async (payload) => {
    setAuthError("");

    if (isKeycloakAuthEnabled()) {
      await startKeycloakLogin({ mode: "register" });
      return null;
    }

    try {
      const nextSession = await authApi.register(payload);
      setSession(nextSession);
      return nextSession;
    } catch (error) {
      const message = getApiErrorMessage(error);
      setAuthError(message);
      throw error;
    }
  }, []);

  const completeExternalLogin = useCallback(async (searchParams) => {
    setAuthError("");

    try {
      const { returnTo, session: tokenSession } = await completeKeycloakLogin(searchParams);
      saveStoredSession(tokenSession);

      const user = await authApi.me();
      const nextSession = {
        ...getStoredSession(),
        user,
      };

      saveStoredSession(nextSession);
      setSession(nextSession);

      return {
        returnTo,
        session: nextSession,
      };
    } catch (error) {
      const message = getApiErrorMessage(error);
      setAuthError(message);
      clearStoredSession();
      throw error;
    }
  }, []);

  const logout = useCallback(async () => {
    const currentSession = getStoredSession();
    const keycloakLogoutUrl =
      currentSession?.authProvider === "keycloak"
        ? buildKeycloakLogoutUrl(currentSession)
        : null;

    setAuthError("");

    try {
      if (!keycloakLogoutUrl && currentSession?.refreshToken) {
        await authApi.logout(currentSession?.refreshToken);
      }
    } finally {
      clearStoredSession();
      setSession(null);

      if (keycloakLogoutUrl) {
        window.location.assign(keycloakLogoutUrl);
      }
    }
  }, []);

  const value = useMemo(
    () => ({
      authError,
      authProvider: AUTH_PROVIDER,
      completeExternalLogin,
      isBooting,
      isAuthenticated: Boolean(session?.accessToken),
      isKeycloakAuth: isKeycloakAuthEnabled(),
      login,
      logout,
      register,
      session,
      user: session?.user ?? null,
    }),
    [authError, completeExternalLogin, isBooting, login, logout, register, session],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }

  return context;
}
