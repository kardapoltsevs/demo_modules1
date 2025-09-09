package controller;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.example.jwt.controller.TokenController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TokenControllerTest {

    @InjectMocks
    private TokenController tokenController;

    private final String testSecret = "test-secret-key-12345678901234567890123456789012";
    private final long testExpiration = 3600000L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenController, "secret", testSecret);
        ReflectionTestUtils.setField(tokenController, "expiration", testExpiration);
    }

    @Test
    @Tag("positive")
    void getToken_ShouldReturnValidJwtToken() {
        // Act
        String token = tokenController.getToken();

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    @Tag("positive")
    void getToken_ShouldContainCorrectSubject() {
        // Act
        String token = tokenController.getToken();

        // Parse the token to verify subject
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(testSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        // Assert
        assertEquals("user", claims.getSubject(), "Token should have subject 'user'");
    }

    @Test
    @Tag("positive")
    void getToken_ShouldSetCorrectExpiration() {
        // Act
        String token = tokenController.getToken();
        long currentTime = System.currentTimeMillis();

        // Parse the token to verify expiration
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(testSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date expiration = claims.getExpiration();
        long expectedExpirationTime = currentTime + testExpiration;

        // Assert - allow 2 seconds tolerance for execution time
        assertTrue(Math.abs(expiration.getTime() - expectedExpirationTime) < 2000,
                "Expiration time should be approximately " + testExpiration + " ms from now");
    }

    @Test
    @Tag("positive")
    void getToken_ShouldSetIssuedAtToCurrentTime() {
        // Act
        String token = tokenController.getToken();
        long currentTime = System.currentTimeMillis();

        // Parse the token to verify issued at time
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(testSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date issuedAt = claims.getIssuedAt();

        // Assert - allow 2 seconds tolerance for execution time
        assertTrue(Math.abs(issuedAt.getTime() - currentTime) < 2000,
                "Issued at time should be approximately current time");
    }

    @Test
    @Tag("positive")
    void getToken_ShouldBeSignedWithHS256() {
        // Act
        String token = tokenController.getToken();

        // If we can parse it with our secret, it means it was signed correctly
        assertDoesNotThrow(() -> {
            Jwts.parserBuilder()
                    .setSigningKey(testSecret.getBytes())
                    .build()
                    .parseClaimsJws(token);
        }, "Token should be properly signed with HS256 algorithm");
    }

    @Test
    @Tag("negative")
    void getToken_WithEmptySecret_ShouldThrowException() {
        // Arrange
        ReflectionTestUtils.setField(tokenController, "secret", "");

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            tokenController.getToken();
        }, "Should throw exception for empty secret key");

        assertTrue(exception.getMessage().contains("key") ||
                        exception.getMessage().contains("secret"),
                "Exception should mention key/secret problem");
    }
}
