package com.acj.acjsignature.mobile.androidws.security;

import com.acj.acjsignature.mobile.androidws.config.AppProperties;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        AppProperties.Jwt jwtProps = new AppProperties.Jwt();
        jwtProps.setSecret("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlq2p5H8Q2uT9R0"); // Just a long string
        jwtProps.setExpiration(3600000L); // 1 hour

        when(appProperties.getJwt()).thenReturn(jwtProps);
    }

    @Test
    void generateAndValidateToken() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user@test.com", "password", Collections.emptyList());

        String token = jwtTokenProvider.generateToken(auth);
        assertNotNull(token);
        assertTrue(token.length() > 0);

        assertTrue(jwtTokenProvider.validateToken(token));

        String username = jwtTokenProvider.getUsernameFromJWT(token);
        assertEquals("user@test.com", username);
    }
}
