package com.jztchl.splitwiseclonejava.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key:change-me-change-me-change-me-change-me-32bytes}")
    private String secret;

    @Value("${security.jwt.expiration-time:3600000}") // 1 hour default in ms
    private long expirationMillis;

    private SecretKey key;

    @PostConstruct
    void init() {
        byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            // pad to meet HS256 minimum key length of 256 bits
            byte[] padded = new byte[32];
            for (int i = 0; i < padded.length; i++) {
                padded[i] = bytes[i % bytes.length];
            }
            bytes = padded;
        }
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    public String generateToken(String email) {
        Date now = new Date();
        Date exp = new Date(System.currentTimeMillis() + expirationMillis);
        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return getAllClaims(token).getSubject();
    }

    public boolean validateToken(String token, String email) {
        try {
            Claims claims = getAllClaims(token);
            String subject = claims.getSubject();
            Date exp = claims.getExpiration();
            return email != null && email.equals(subject) && exp != null && exp.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }


    private Claims getAllClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
