package org.hotel.app.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    private UserDetails userDetails;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("admin");
    }

    @Test
    void generateToken_ShouldReturnValidTokenString() {
        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void extractUsername_ShouldReturnCorrectUsernameFromToken() {
        String token = jwtService.generateToken(userDetails);

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals("admin", extractedUsername);
    }

    @Test
    void isTokenValid_ShouldReturnTrue_WhenTokenMatchesUser() {
        String token = jwtService.generateToken(userDetails);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void isTokenValid_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
        String adminToken = jwtService.generateToken(userDetails);

        UserDetails other = mock(UserDetails.class);
        when(other.getUsername()).thenReturn("abc");

        assertFalse(jwtService.isTokenValid(adminToken, other));
    }

    @Test
    void generateToken_WithExtraClaims_ShouldStillBeValid() {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", "SUPER_ADMIN");

        String token = jwtService.generateToken(extraClaims, userDetails);

        assertNotNull(token);
        assertTrue(jwtService.isTokenValid(token, userDetails));
        assertEquals("admin", jwtService.extractUsername(token));
    }
}
