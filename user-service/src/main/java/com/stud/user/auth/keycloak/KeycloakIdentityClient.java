package com.stud.user.auth.keycloak;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.stud.user.auth.config.KeycloakClientProperties;
import com.stud.user.auth.web.dto.AuthDtos.RegisterRequest;
import com.stud.user.common.exception.DuplicateResourceException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "app.security", name = "provider", havingValue = "keycloak")
public class KeycloakIdentityClient {

    private static final String CLIENT_ROLE = "CLIENT";

    private final KeycloakClientProperties properties;
    private final RestClient restClient;

    public KeycloakIdentityClient(KeycloakClientProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClient = restClientBuilder.baseUrl(properties.serverUrl()).build();
    }

    public String createUser(RegisterRequest request) {
        String serviceToken = requestServiceToken();
        NameParts name = splitName(request);
        Map<String, Object> representation = Map.of(
                "username", request.username().trim(),
                "email", request.email().trim().toLowerCase(),
                "enabled", true,
                "emailVerified", false,
                "firstName", name.firstName(),
                "lastName", name.lastName(),
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", request.password(),
                        "temporary", false
                ))
        );

        try {
            ResponseEntity<Void> response = restClient.post()
                    .uri("/admin/realms/{realm}/users", properties.realm())
                    .headers(headers -> headers.setBearerAuth(serviceToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(representation)
                    .retrieve()
                    .toBodilessEntity();

            String userId = extractCreatedId(response.getHeaders().getLocation());
            assignRealmRole(serviceToken, userId, CLIENT_ROLE);
            return userId;
        } catch (HttpClientErrorException.Conflict exception) {
            throw new DuplicateResourceException("User already exists in Keycloak");
        }
    }

    public TokenResponse login(String email, String password) {
        MultiValueMap<String, String> form = clientForm();
        form.add("grant_type", "password");
        form.add("username", email);
        form.add("password", password);
        form.add("scope", "openid profile email");

        try {
            return requestToken(form);
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new BadCredentialsException("Invalid email or password", exception);
        }
    }

    public TokenResponse refresh(String refreshToken) {
        MultiValueMap<String, String> form = clientForm();
        form.add("grant_type", "refresh_token");
        form.add("refresh_token", refreshToken);

        try {
            return requestToken(form);
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new BadCredentialsException("Invalid refresh token", exception);
        }
    }

    public void logout(String refreshToken) {
        MultiValueMap<String, String> form = clientForm();
        form.add("refresh_token", refreshToken);

        try {
            restClient.post()
                    .uri("/realms/{realm}/protocol/openid-connect/logout", properties.realm())
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest exception) {
            throw new BadCredentialsException("Invalid refresh token", exception);
        }
    }

    public void deleteUser(String userId) {
        try {
            restClient.delete()
                    .uri("/admin/realms/{realm}/users/{userId}", properties.realm(), userId)
                    .headers(headers -> headers.setBearerAuth(requestServiceToken()))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RuntimeException ignored) {
            // Best-effort compensation for a failed local transaction.
        }
    }

    private String requestServiceToken() {
        MultiValueMap<String, String> form = clientForm();
        form.add("grant_type", "client_credentials");
        return requestToken(form).accessToken();
    }

    private TokenResponse requestToken(MultiValueMap<String, String> form) {
        TokenResponse response = restClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", properties.realm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(TokenResponse.class);

        if (response == null || response.accessToken() == null) {
            throw new IllegalStateException("Keycloak returned an empty token response");
        }

        return response;
    }

    @SuppressWarnings("unchecked")
    private void assignRealmRole(String serviceToken, String userId, String roleName) {
        Map<String, Object> role = restClient.get()
                .uri("/admin/realms/{realm}/roles/{roleName}", properties.realm(), roleName)
                .headers(headers -> headers.setBearerAuth(serviceToken))
                .retrieve()
                .body(Map.class);

        if (role == null) {
            throw new IllegalStateException("Keycloak realm role not found: " + roleName);
        }

        try {
            restClient.post()
                    .uri(
                            "/admin/realms/{realm}/users/{userId}/role-mappings/realm",
                            properties.realm(),
                            userId
                    )
                    .headers(headers -> headers.setBearerAuth(serviceToken))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(List.of(role))
                    .retrieve()
                    .toBodilessEntity();
        } catch (RuntimeException exception) {
            deleteUser(userId);
            throw exception;
        }
    }

    private MultiValueMap<String, String> clientForm() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", properties.clientId());
        form.add("client_secret", properties.clientSecret());
        return form;
    }

    private String extractCreatedId(URI location) {
        if (location == null || location.getPath() == null) {
            throw new IllegalStateException("Keycloak did not return the created user location");
        }

        String path = location.getPath();
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private NameParts splitName(RegisterRequest request) {
        String fallback = request.username().trim();
        String displayName = request.displayName() == null || request.displayName().isBlank()
                ? fallback
                : request.displayName().trim();
        String[] parts = displayName.split("\\s+", 2);

        return new NameParts(parts[0], parts.length == 2 ? parts[1] : fallback);
    }

    private record NameParts(String firstName, String lastName) {
    }

    public record TokenResponse(
            @JsonProperty("token_type") String tokenType,
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("refresh_token") String refreshToken,
            @JsonProperty("expires_in") long expiresInSeconds
    ) {
    }
}
