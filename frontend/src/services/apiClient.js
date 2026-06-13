import axios from "axios";
import { refreshKeycloakSession } from "./oidcClient.js";

const AUTH_STORAGE_KEY = "bountyGuild.session";
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "/api/v1";
const mojibakeDecoder = new TextDecoder("utf-8");

export const publicClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

export function getStoredSession() {
  const rawSession = localStorage.getItem(AUTH_STORAGE_KEY);

  if (!rawSession) {
    return null;
  }

  try {
    return JSON.parse(rawSession);
  } catch {
    localStorage.removeItem(AUTH_STORAGE_KEY);
    return null;
  }
}

export function saveStoredSession(session) {
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(session));
  window.dispatchEvent(new Event("bountyGuild:session"));
}

export function clearStoredSession() {
  localStorage.removeItem(AUTH_STORAGE_KEY);
  window.dispatchEvent(new Event("bountyGuild:session"));
}

function repairMojibake(value) {
  if (typeof value !== "string" || !/[ÐÑ][\u0080-\u00bf]/.test(value)) {
    return value;
  }

  const bytes = Uint8Array.from(value, (char) => char.charCodeAt(0) & 0xff);
  const repaired = mojibakeDecoder.decode(bytes);

  return /[А-Яа-яЁё]/.test(repaired) ? repaired : value;
}

function normalizePayload(payload) {
  if (Array.isArray(payload)) {
    return payload.map(normalizePayload);
  }

  if (payload && typeof payload === "object") {
    return Object.fromEntries(
      Object.entries(payload).map(([key, value]) => [key, normalizePayload(value)]),
    );
  }

  return repairMojibake(payload);
}

function normalizeResponse(response) {
  response.data = normalizePayload(response.data);
  return response;
}

publicClient.interceptors.response.use(normalizeResponse);

apiClient.interceptors.request.use((config) => {
  const session = getStoredSession();

  if (session?.accessToken) {
    config.headers.Authorization = `Bearer ${session.accessToken}`;
  }

  return config;
});

let refreshRequest = null;

apiClient.interceptors.response.use(normalizeResponse);

apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    const session = getStoredSession();

    if (
      error.response?.status !== 401 ||
      originalRequest?._retry ||
      !session?.refreshToken
    ) {
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    try {
      refreshRequest ??= (
        session.authProvider === "keycloak"
          ? refreshKeycloakSession(session.refreshToken)
          : publicClient
              .post("/auth/refresh", { refreshToken: session.refreshToken })
              .then((response) => response.data)
      ).finally(() => {
        refreshRequest = null;
      });

      const refreshedSession = await refreshRequest;
      saveStoredSession({
        ...session,
        ...refreshedSession,
      });

      originalRequest.headers.Authorization = `Bearer ${refreshedSession.accessToken}`;
      return apiClient(originalRequest);
    } catch (refreshError) {
      clearStoredSession();
      return Promise.reject(refreshError);
    }
  },
);

export function getApiErrorMessage(error) {
  return (
    error?.response?.data?.message ||
    error?.response?.data?.detail ||
    error?.response?.data?.error ||
    error?.message ||
    "Запрос не выполнен"
  );
}
