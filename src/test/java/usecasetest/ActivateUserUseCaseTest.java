package usecasetest;

import com.ramaccioni.api_clean_arch.core.dto.ActivateUserDTO;
import com.ramaccioni.api_clean_arch.core.enums.UserStatus;
import com.ramaccioni.api_clean_arch.core.exceptions.*;
import com.ramaccioni.api_clean_arch.core.model.User;
import com.ramaccioni.api_clean_arch.core.output.IUserRepository;
import com.ramaccioni.api_clean_arch.core.usecase.ActivateUserUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivateUserUseCaseTest {

    @Mock
    private IUserRepository userRepository;

    private Clock fixedClock;
    private ActivateUserUseCase useCase;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-01-01T10:00:00Z"), ZoneId.of("UTC"));
        useCase = new ActivateUserUseCase(userRepository, fixedClock);
    }

    @Test
    void testExecute_HappyPath() {
        // Arrange
        User realUser = User.create("test@example.com", "hash", 30, fixedClock);
        Long userId = 1L;

        // Obtenemos el codigo real que fue generado
        String correctCode = realUser.getActivationCode();

        ActivateUserDTO dto = new ActivateUserDTO(userId, correctCode);

        // Encontramos el coso
        when(userRepository.findById(userId)).thenReturn(Optional.of(realUser));

        // Act
        useCase.execute(dto);

        // Assert: usuario cambio a ACTIVE
        Assertions.assertEquals(UserStatus.ACTIVE, realUser.getStatus());
        Assertions.assertNull(realUser.getActivationCode());
        Assertions.assertNull(realUser.getActivationExpiresAt());

        // Verificamos que sea el usuario modificado
        verify(userRepository).save(realUser);
    }

    @Test
    void testExecute_UserNotFound() {
        // Arrange
        ActivateUserDTO dto = new ActivateUserDTO(99L, "some-code");
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        Assertions.assertThrows(UserNotFoundException.class, () -> useCase.execute(dto));

        verify(userRepository, never()).save(any());
    }

    @Test
    void testExecute_InvalidCode_ShouldPropagateException() {
        // Arrange
        User realUser = User.create("test@example.com", "hash", 30, fixedClock);
        ActivateUserDTO dto = new ActivateUserDTO(1L, "WRONG_CODE_XYZ");

        when(userRepository.findById(1L)).thenReturn(Optional.of(realUser));

        // Act & Assert: tiene que salir la exception
        Assertions.assertThrows(InvalidActivationCodeException.class, () ->
                useCase.execute(dto)
        );

        // El usuario NO debe guardarse si salto la exeption
        verify(userRepository, never()).save(any());
    }

    @Test
    void testExecute_ExpiredCode_ShouldPropagateException() {
        // Arrange
        User realUser = User.create("test@example.com", "hash", 30, fixedClock);
        String correctCode = realUser.getActivationCode();
        ActivateUserDTO dto = new ActivateUserDTO(1L, correctCode);

        when(userRepository.findById(1L)).thenReturn(Optional.of(realUser));

        //
        Clock futureClock = Clock.fixed(Instant.parse("2026-01-01T10:31:00Z"), ZoneId.of("UTC"));
        ActivateUserUseCase futureUseCase = new ActivateUserUseCase(userRepository, futureClock);

        // Act & Assert
        Assertions.assertThrows(ActivationExpiredException.class, () ->
                futureUseCase.execute(dto)
        );

        // Verificamos
        verify(userRepository, never()).save(any());
    }

    @Test
    void testExecute_MissingRequiredFields_NullDto() {
        Assertions.assertThrows(MissingRequiredFieldException.class, () -> useCase.execute(null));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testExecute_MissingRequiredFields_NullUserId() {
        ActivateUserDTO dto = new ActivateUserDTO(null, "code");
        Assertions.assertThrows(MissingRequiredFieldException.class, () -> useCase.execute(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testExecute_MissingRequiredFields_NullCode() {
        ActivateUserDTO dto = new ActivateUserDTO(1L, null);
        Assertions.assertThrows(MissingRequiredFieldException.class, () -> useCase.execute(dto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void testExecute_UserAlreadyActive_ShouldThrowUserAlreadyActiveException() {
        User realUser = User.create("test@example.com", "hash", 30, fixedClock);
        String code = realUser.getActivationCode();
        realUser.activate(code, fixedClock);

        ActivateUserDTO dto = new ActivateUserDTO(1L, "any-code");
        when(userRepository.findById(1L)).thenReturn(Optional.of(realUser));

        Assertions.assertThrows(UserAlreadyActiveException.class, () -> useCase.execute(dto));
        verify(userRepository, never()).save(any());
    }
}
