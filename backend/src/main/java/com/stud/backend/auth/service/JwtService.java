package com.stud.backend.auth.service;


import com.stud.backend.auth.config.JwtProperties;
import com.stud.backend.users.domain.User;
import com.stud.backend.users.domain.enums.RoleName;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    public String generateAccessToken(User user, Set<RoleName> roles) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(getAccessTokenExpirationSeconds());

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("uid", user.getId().toString())
                .claim("roles", roles.stream().map(RoleName::name).toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String extractSubject(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, String expectedEmail) {
        Claims claims = extractAllClaims(token);
        String subject = claims.getSubject();
        Date expiration = claims.getExpiration();

        return subject.equalsIgnoreCase(expectedEmail) && expiration.after(new Date());
    }

    public long getAccessTokenExpirationSeconds() {
        return jwtProperties.accessTokenExpirationMinutes() * 60;
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
