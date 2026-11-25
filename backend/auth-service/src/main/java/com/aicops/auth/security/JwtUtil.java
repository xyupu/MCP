package com.aicops.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * JWT 工具：签发与解析。
 */
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expireSeconds;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expire-seconds}") long expireSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expireSeconds = expireSeconds;
    }

    public String generateToken(Long userId, String username, Long tenantId, List<String> roles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expireSeconds * 1000);
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .claim("tenantId", tenantId)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpireSeconds() {
        return expireSeconds;
    }
}
