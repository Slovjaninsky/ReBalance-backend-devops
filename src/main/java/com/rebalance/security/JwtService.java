package com.rebalance.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rebalance.exception.RebalanceErrorType;
import com.rebalance.exception.RebalanceException;

import java.security.Key;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String secretKey;
    private final TokenRepository tokenRepository;

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token); // Validate the token signature
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject(); // Typically the "sub" claim
    }

    public List<String> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("cognito:groups", List.class); // Example of extracting groups/roles
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public void saveToken(Long userId, String jwtToken) {
        Token token = new Token();
        token.setUserId(userId);
        token.setToken(jwtToken);
        tokenRepository.save(token);
    }

    public void deleteToken(String token) {
        Token savedToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RebalanceException(RebalanceErrorType.RB_401));
        tokenRepository.delete(savedToken);
    }
}
