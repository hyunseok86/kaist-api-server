package com.kaist.api.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Objects;

@Component
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret:kaistbluejwtsecretkey123456789012345678901234567890}")
    private String secret;

    @Value("${jwt.expiration:86400000}") // 24시간
    private Long expiration;

    private SecretKey deriveKey(byte[] keyBytes) {
        if (keyBytes == null) {
            return Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
        if (keyBytes.length >= 64) {
            return Keys.hmacShaKeyFor(keyBytes);
        }
        try {
            MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
            byte[] digest = sha512.digest(keyBytes); // 64 bytes
            return Keys.hmacShaKeyFor(digest);
        } catch (NoSuchAlgorithmException e) {
            // Fallback: generate strong random key (not ideal for multi-instance)
            log.warn("SHA-512 not available, generating random HS512 key: {}", e.getMessage());
            return Keys.secretKeyFor(SignatureAlgorithm.HS512);
        }
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = null;
        if (secret != null) {
            String trimmed = secret.trim();
            // Allow providing Base64-encoded secrets by prefix
            if (trimmed.startsWith("base64:")) {
                String b64 = trimmed.substring("base64:".length());
                try {
                    keyBytes = java.util.Base64.getDecoder().decode(b64);
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid Base64 jwt.secret, falling back to UTF-8 bytes");
                    keyBytes = trimmed.getBytes(StandardCharsets.UTF_8);
                }
            } else {
                keyBytes = trimmed.getBytes(StandardCharsets.UTF_8);
            }
        }
        return deriveKey(keyBytes);
    }

    public String generateToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + (Objects.requireNonNullElse(expiration, 86400000L)));

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}

