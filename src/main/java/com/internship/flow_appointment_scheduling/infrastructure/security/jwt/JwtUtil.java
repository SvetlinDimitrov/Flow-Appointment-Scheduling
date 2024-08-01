package com.internship.flow_appointment_scheduling.infrastructure.security.jwt;

import com.internship.flow_appointment_scheduling.infrastructure.security.dto.JwtResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    public JwtResponse generateToken(String email) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);

        String token = Jwts.builder()
            .setSubject(email)
            .setIssuedAt(now)
            .setExpiration(expirationDate)
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();

        LocalDateTime expirationTime = LocalDateTime.ofInstant(expirationDate.toInstant(), ZoneId.systemDefault());

        return new JwtResponse(token, expirationTime);
    }

    public Boolean isTokenExpired(String token) {
        Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}