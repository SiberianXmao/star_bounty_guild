export const AUTH_PROVIDER = (import.meta.env.VITE_AUTH_PROVIDER ?? "local").toLowerCase();

export const keycloakConfig = {
  url: import.meta.env.VITE_KEYCLOAK_URL ?? "http://localhost:8085",
  realm: import.meta.env.VITE_KEYCLOAK_REALM ?? "bounty-guild",
  clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID ?? "bounty-frontend",
  callbackPath: "/auth/callback",
};

export function isKeycloakAuthEnabled() {
  return AUTH_PROVIDER === "keycloak";
}
