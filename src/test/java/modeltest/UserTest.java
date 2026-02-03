package modeltest;

import com.ramaccioni.api_clean_arch.core.enums.UserStatus;
import com.ramaccioni.api_clean_arch.core.exceptions.ActivationExpiredException;
import com.ramaccioni.api_clean_arch.core.exceptions.InvalidActivationCodeException;
import com.ramaccioni.api_clean_arch.core.exceptions.UserNotPendingException;
import com.ramaccioni.api_clean_arch.core.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

class UserTest {
    private Clock fixedClock;

    private static final String VALID_EMAIL = "test@example.com";
    private static final String VALID_PASS_HASH = "hashed_secret";
    private static final ZoneId UTC = ZoneId.of("UTC");

    @BeforeEach
    void setUp() {
        // Tiempo fijo: 2026-01-01 10:00:00 UTC
        fixedClock = Clock.fixed(Instant.parse("2026-01-01T10:00:00Z"), UTC);
    }

    @Test
    void testCreateUser_HappyPath() {
        // Arrange & Act
        int expirationMinutes = 30;
        User user = User.create(VALID_EMAIL, VALID_PASS_HASH, expirationMinutes, fixedClock);

        // Assert
        Assertions.assertNotNull(user);
        Assertions.assertEquals(UserStatus.PENDING, user.getStatus());
        Assertions.assertNotNull(user.getActivationCode());

        // createdAt debe ser exactamente "ahora" del clock fijo
        Assertions.assertEquals(
                LocalDateTime.of(2026, 1, 1, 10, 0, 0),
                user.getCreatedAt()
        );

        // activationExpiresAt debe ser exactamente 30 min después
        Assertions.assertEquals(
                LocalDateTime.of(2026, 1, 1, 10, 30, 0),
                user.getActivationExpiresAt()
        );
    }

    @Test
    void testActivateUser_HappyPath() {
        // Arrange
        User user = User.create(VALID_EMAIL, VALID_PASS_HASH, 30, fixedClock);
        String code = user.getActivationCode();

        // Act
        user.activate(code, fixedClock);

        // Assert
        Assertions.assertEquals(UserStatus.ACTIVE, user.getStatus());
        Assertions.assertNull(user.getActivationCode());
        Assertions.assertNull(user.getActivationExpiresAt());
    }

    @Test
    void testActivateUser_InvalidActivationCode() {
        // Arrange
        User user = User.create(VALID_EMAIL, VALID_PASS_HASH, 30, fixedClock);

        // Assert
        Assertions.assertThrows(InvalidActivationCodeException.class, () ->
                user.activate("WRONG_CODE_123", fixedClock)
        );
    }

    @Test
    void testActivateUser_UserNotPending() {
        // Arrange
        User user = User.create(VALID_EMAIL, VALID_PASS_HASH, 30, fixedClock);
        user.activate(user.getActivationCode(), fixedClock); // queda ACTIVE

        // Assert: intentar activar de nuevo
        Assertions.assertThrows(UserNotPendingException.class, () ->
                user.activate("ANY_CODE", fixedClock)
        );
    }

    @Test
    void testActivateUser_ExpiredCode() {
        // Arrange: expira a las 10:30
        User user = User.create(VALID_EMAIL, VALID_PASS_HASH, 30, fixedClock);
        String code = user.getActivationCode();

        // Simulamos 31 minutos después (10:31)
        Clock futureClock = Clock.fixed(Instant.parse("2026-01-01T10:31:00Z"), UTC);

        // Assert
        Assertions.assertThrows(ActivationExpiredException.class, () ->
                user.activate(code, futureClock)
        );
    }
}
