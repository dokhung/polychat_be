package com.example.polychat_be.auth.service;

import com.example.polychat_be.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey key;
    private final long accessExpiration;
    private final long refreshExpiration;

    public JwtService(@Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-expiration-ms}") long accessExpiration,
            @Value("${app.jwt.refresh-expiration-ms}") long refreshExpiration
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
    }

    public String createAccessToken(User user) {
        return createToken(user, accessExpiration, "access");
    }

    public String createRefreshToken(User user) {
        return createToken(user, refreshExpiration, "refresh");
    }

    public long getAccessExpiration() {
        return accessExpiration;
    }

    public String subject(String token) {
        return parse(token).getPayload().getSubject();
    }

    public boolean isRefresh(String token) {
        return "refresh".equals(parse(token).getPayload().get("type"));
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }

    private String createToken(User user, long duration, String type) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("type", type)
                .issuedAt(Date.from(now)).expiration(Date.from(now.plusMillis(duration)))
                .signWith(key)
                .compact();
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }
}
