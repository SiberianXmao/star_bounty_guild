import { keycloakConfig } from "../config/auth.js";

const OIDC_FLOW_STORAGE_KEY = "bountyGuild.oidc.flow";

function getRealmBaseUrl() {
  return `${keycloakConfig.url}/realms/${keycloakConfig.realm}`;
}

function getCallbackUrl() {
  return `${window.location.origin}${keycloakConfig.callbackPath}`;
}

function encodeBase64Url(bytes) {
  const binary = Array.from(bytes, (byte) => String.fromCharCode(byte)).join("");

  return btoa(binary)
    .replace(/\+/g, "-")
    .replace(/\//g, "_")
    .replace(/=+$/g, "");
}

function createRandomToken(byteLength = 48) {
  const bytes = new Uint8Array(byteLength);
  crypto.getRandomValues(bytes);
  return encodeBase64Url(bytes);
}

async function createCodeChallenge(verifier) {
  const bytes = new TextEncoder().encode(verifier);
  const digest = await crypto.subtle.digest("SHA-256", bytes);
  return encodeBase64Url(new Uint8Array(digest));
}

function saveOidcFlow(flow) {
  sessionStorage.setItem(OIDC_FLOW_STORAGE_KEY, JSON.stringify(flow));
}

function takeOidcFlow() {
  const rawFlow = sessionStorage.getItem(OIDC_FLOW_STORAGE_KEY);
  sessionStorage.removeItem(OIDC_FLOW_STORAGE_KEY);

  if (!rawFlow) {
    throw new Error("OIDC session was not found. Start login again.");
  }

  return JSON.parse(rawFlow);
}

function toTokenSession(tokenResponse) {
  return {
    authProvider: "keycloak",
    tokenType: tokenResponse.token_type ?? "Bearer",
    accessToken: tokenResponse.access_token,
    refreshToken: tokenResponse.refresh_token,
    idToken: tokenResponse.id_token,
    expiresInSeconds: tokenResponse.expires_in,
  };
}

async function requestTokens(payload) {
  const response = await fetch(`${getRealmBaseUrl()}/protocol/openid-connect/token`, {
    method: "POST",
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
    },
    body: payload,
  });

  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(data.error_description || data.error || "Keycloak token request failed");
  }

  return toTokenSession(data);
}

export async function startKeycloakLogin({ mode = "login", returnTo = "/cabinet" } = {}) {
  const state = createRandomToken(32);
  const nonce = createRandomToken(32);
  const codeVerifier = createRandomToken(64);
  const codeChallenge = await createCodeChallenge(codeVerifier);
  const endpoint = mode === "register" ? "registrations" : "auth";
  const authUrl = new URL(`${getRealmBaseUrl()}/protocol/openid-connect/${endpoint}`);

  authUrl.searchParams.set("client_id", keycloakConfig.clientId);
  authUrl.searchParams.set("redirect_uri", getCallbackUrl());
  authUrl.searchParams.set("response_type", "code");
  authUrl.searchParams.set("scope", "openid profile email");
  authUrl.searchParams.set("state", state);
  authUrl.searchParams.set("nonce", nonce);
  authUrl.searchParams.set("code_challenge", codeChallenge);
  authUrl.searchParams.set("code_challenge_method", "S256");

  saveOidcFlow({
    codeVerifier,
    nonce,
    returnTo,
    state,
  });

  window.location.assign(authUrl.toString());
}

export async function completeKeycloakLogin(searchParams) {
  const error = searchParams.get("error");

  if (error) {
    throw new Error(searchParams.get("error_description") || error);
  }

  const code = searchParams.get("code");
  const state = searchParams.get("state");
  const flow = takeOidcFlow();

  if (!code) {
    throw new Error("Keycloak did not return an authorization code.");
  }

  if (!state || state !== flow.state) {
    throw new Error("OIDC state mismatch. Start login again.");
  }

  const payload = new URLSearchParams({
    grant_type: "authorization_code",
    client_id: keycloakConfig.clientId,
    redirect_uri: getCallbackUrl(),
    code,
    code_verifier: flow.codeVerifier,
  });

  return {
    returnTo: flow.returnTo,
    session: await requestTokens(payload),
  };
}

export async function refreshKeycloakSession(refreshToken) {
  const payload = new URLSearchParams({
    grant_type: "refresh_token",
    client_id: keycloakConfig.clientId,
    refresh_token: refreshToken,
  });

  return requestTokens(payload);
}

export function buildKeycloakLogoutUrl(session) {
  const logoutUrl = new URL(`${getRealmBaseUrl()}/protocol/openid-connect/logout`);

  logoutUrl.searchParams.set("client_id", keycloakConfig.clientId);
  logoutUrl.searchParams.set("post_logout_redirect_uri", window.location.origin);

  if (session?.idToken) {
    logoutUrl.searchParams.set("id_token_hint", session.idToken);
  }

  return logoutUrl.toString();
}
