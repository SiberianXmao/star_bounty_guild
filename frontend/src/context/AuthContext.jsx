import { createContext, useCallback, useContext, useEffect, useMemo, useState } from "react";
import { authApi } from "../services/bountyApi.js";
import {
  clearStoredSession,
  getApiErrorMessage,
  getStoredSession,
  saveStoredSession,
} from "../services/apiClient.js";

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

  const logout = useCallback(async () => {
    const currentSession = getStoredSession();
    setAuthError("");

    try {
      await authApi.logout(currentSession?.refreshToken);
    } finally {
      clearStoredSession();
      setSession(null);
    }
  }, []);

  const value = useMemo(
    () => ({
      authError,
      isBooting,
      isAuthenticated: Boolean(session?.accessToken),
      login,
      logout,
      register,
      session,
      user: session?.user ?? null,
    }),
    [authError, isBooting, login, logout, register, session],
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
