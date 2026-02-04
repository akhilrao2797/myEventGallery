package com.example.myeventgallery.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public String generateCustomerToken(String email, Long customerId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("customerId", customerId);
        claims.put("type", "CUSTOMER");
        return createToken(claims, email);
    }
    
    public String generateGuestToken(String email, Long guestId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("guestId", guestId);
        claims.put("type", "GUEST");
        return createToken(claims, email);
    }
    
    // Legacy method for backward compatibility
    public String generateToken(String email, Long customerId) {
        return generateCustomerToken(email, customerId);
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }
    
    public Long extractCustomerId(String token) {
        return extractClaims(token).get("customerId", Long.class);
    }
    
    public Long extractGuestId(String token) {
        return extractClaims(token).get("guestId", Long.class);
    }
    
    public String extractUserType(String token) {
        Object type = extractClaims(token).get("type");
        return type != null ? type.toString() : "CUSTOMER"; // Default to CUSTOMER for backward compatibility
    }
    
    public Date extractExpiration(String token) {
        return extractClaims(token).getExpiration();
    }
    
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public Boolean validateToken(String token, String email) {
        final String extractedEmail = extractEmail(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }
    
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }
}
