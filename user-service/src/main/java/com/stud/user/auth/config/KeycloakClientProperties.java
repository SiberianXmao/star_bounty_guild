package com.stud.user.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.keycloak")
public record KeycloakClientProperties(
        String serverUrl,
        String realm,
        String clientId,
        String clientSecret
) {
}
