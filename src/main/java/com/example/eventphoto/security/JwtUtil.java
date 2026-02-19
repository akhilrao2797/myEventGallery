package com.example.eventphoto.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:eventPhotoAppSecretKeyForJWTTokenGeneration2024Minimum32Characters}")
    private String secret;

    @Value("${jwt.expiration-ms:86400000}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateCustomerToken(String email, Long customerId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("customerId", customerId)
                .claim("type", "CUSTOMER")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateGuestToken(String email, Long guestId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("guestId", guestId)
                .claim("type", "GUEST")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAdminToken(String email, Long adminId) {
        return Jwts.builder()
                .setSubject(email)
                .claim("adminId", adminId)
                .claim("type", "ADMIN")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getCustomerIdFromToken(String token) {
        Claims claims = parseToken(token);
        Object id = claims.get("customerId");
        return id instanceof Integer ? ((Integer) id).longValue() : (Long) id;
    }

    public Long getGuestIdFromToken(String token) {
        Claims claims = parseToken(token);
        Object id = claims.get("guestId");
        return id instanceof Integer ? ((Integer) id).longValue() : (Long) id;
    }

    public Long getAdminIdFromToken(String token) {
        Claims claims = parseToken(token);
        Object id = claims.get("adminId");
        return id instanceof Integer ? ((Integer) id).longValue() : (Long) id;
    }

    public String getEmailFromToken(String token) {
        return parseToken(token).getSubject();
    }
}
