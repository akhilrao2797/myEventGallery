package com.example.myeventgallery.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "mySecretKeyForJWTTokenGenerationWhichShouldBeLongAndSecure");
        ReflectionTestUtils.setField(jwtUtil, "expiration", 86400000L);
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken("test@example.com", 1L);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void testExtractEmail() {
        String token = jwtUtil.generateToken("test@example.com", 1L);
        String email = jwtUtil.extractEmail(token);

        assertEquals("test@example.com", email);
    }

    @Test
    void testExtractCustomerId() {
        String token = jwtUtil.generateToken("test@example.com", 1L);
        Long customerId = jwtUtil.extractCustomerId(token);

        assertEquals(1L, customerId);
    }

    @Test
    void testValidateToken() {
        String token = jwtUtil.generateToken("test@example.com", 1L);
        Boolean isValid = jwtUtil.validateToken(token, "test@example.com");

        assertTrue(isValid);
    }

    @Test
    void testValidateTokenInvalidEmail() {
        String token = jwtUtil.generateToken("test@example.com", 1L);
        Boolean isValid = jwtUtil.validateToken(token, "wrong@example.com");

        assertFalse(isValid);
    }

    @Test
    void testIsTokenExpired() {
        String token = jwtUtil.generateToken("test@example.com", 1L);
        Boolean isExpired = jwtUtil.isTokenExpired(token);

        assertFalse(isExpired);
    }
}
