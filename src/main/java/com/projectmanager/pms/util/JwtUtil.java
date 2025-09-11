package com.projectmanager.pms.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility class for JWT (JSON Web Token) operations
 * Handles token generation, validation, and extraction of claims
 */
@Component
public class JwtUtil {

    // JWT secret key from application.yml
    @Value("${app.jwt.secret}")
    private String jwtSecret;

    // JWT expiration time from application.yml (in milliseconds)
    @Value("${app.jwt.expiration}")
    private Long jwtExpirationMs;

    /**
     * Generate secret key for JWT signing
     * @return SecretKey for HMAC-SHA algorithms
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Extract username (email) from JWT token
     * @param token - JWT token
     * @return username/email from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from JWT token
     * @param token - JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract specific claim from JWT token
     * @param token - JWT token
     * @param claimsResolver - function to extract specific claim
     * @return extracted claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from JWT token
     * @param token - JWT token
     * @return all claims from token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage());
        }
    }

    /**
     * Check if token is expired
     * @param token - JWT token
     * @return true if token is expired
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generate JWT token for user
     * @param userDetails - Spring Security UserDetails
     * @return generated JWT token
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Generate JWT token with additional claims
     * @param userDetails - Spring Security UserDetails
     * @param userId - user ID to include in token
     * @param role - user role to include in token
     * @return generated JWT token with custom claims
     */
    public String generateToken(UserDetails userDetails, Long userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("fullName", extractFullNameFromUserDetails(userDetails));
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Extract full name from UserDetails (if available)
     * We'll implement this when we create our custom UserDetails
     * @param userDetails - Spring Security UserDetails
     * @return full name or empty string
     */
    private String extractFullNameFromUserDetails(UserDetails userDetails) {
        // For now, return empty string. We'll enhance this later
        // when we implement our custom UserDetails class
        return userDetails.getUsername(); // Return username for now
    }

    /**
     * Create JWT token with claims and subject
     * @param claims - additional claims to include
     * @param subject - token subject (username/email)
     * @return created JWT token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    /**
     * Validate JWT token against UserDetails
     * @param token - JWT token
     * @param userDetails - Spring Security UserDetails
     * @return true if token is valid
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract user ID from JWT token
     * @param token - JWT token
     * @return user ID
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Long.class);
    }

    /**
     * Extract user role from JWT token
     * @param token - JWT token
     * @return user role
     */
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * Check if token is valid (not expired and properly formatted)
     * @param token - JWT token
     * @return true if token is valid
     */
    public Boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get expiration time in milliseconds
     * @return expiration time
     */
    public Long getExpirationMs() {
        return jwtExpirationMs;
    }
}