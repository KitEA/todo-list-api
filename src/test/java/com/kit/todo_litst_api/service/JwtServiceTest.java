package com.kit.todo_litst_api.service;

import com.kit.todo_litst_api.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private JwtService jwtService;

    private SecretKey key;

    @BeforeEach
    void setUp() {
        String secretKeyString = "this-is-a-very-long-secret-key-for-testing-purposes";
        jwtService = new JwtService(secretKeyString);
        key = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void shouldGenerateValidToken() {
        // Given
        User user = User.builder()
                .id(4L)
                .email("testuser@example.com")
                .password("1234")
                .username("testuser")
                .build();

        // When
        String token = jwtService.generateToken(user);

        // Then
        assertThat(token).isNotBlank();

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertThat(claims.getSubject()).isEqualTo("testuser@example.com");
        assertThat(claims.get("username")).isEqualTo("testuser");
    }

    @Test
    void shouldFailOnExpiredToken() {
        // Given
        String expiredToken = Jwts.builder()
                .subject("test@example.com")
                .issuedAt(new Date(System.currentTimeMillis() - 10000))
                .expiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(key)
                .compact();

        // When & Then
        assertThatThrownBy(() -> Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void shouldFailOnInvalidTokenSignature() {
        // Given
        User user = User.builder().id(1L).email("test@example.com").build();
        String token = jwtService.generateToken(user);

        String tamperedToken = token.substring(0, token.length() - 5) + "XXXXX";

        // When & Then
        assertThatThrownBy(() -> Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(tamperedToken))
                .isInstanceOf(SignatureException.class);
    }
}
