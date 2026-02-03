package usecasetest;

import com.ramaccioni.api_clean_arch.core.dto.RegisterUserDTO;
import com.ramaccioni.api_clean_arch.core.exceptions.EmailAlreadyExistsException;
import com.ramaccioni.api_clean_arch.core.exceptions.InvalidEmailException;
import com.ramaccioni.api_clean_arch.core.exceptions.InvalidPasswordException;
import com.ramaccioni.api_clean_arch.core.model.User;
import com.ramaccioni.api_clean_arch.core.output.IActivationCodeSender;
import com.ramaccioni.api_clean_arch.core.output.IPasswordHasher;
import com.ramaccioni.api_clean_arch.core.output.IUserRepository;
import com.ramaccioni.api_clean_arch.core.usecase.RegisterUserUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest{

    @Mock
    private IUserRepository userRepository;
    @Mock
    private IActivationCodeSender activationCodeSender;
    @Mock
    private IPasswordHasher passwordHasher;

    private Clock fixedClock;
    private RegisterUserUseCase useCase;

    @BeforeEach
    void setUp() {
        fixedClock = Clock.fixed(Instant.parse("2026-01-01T10:00:00Z"), ZoneId.of("UTC"));
        useCase = new RegisterUserUseCase(userRepository, activationCodeSender, passwordHasher, 30, fixedClock);
    }

    @Test
    void testExecute_HappyPath() {
        // Arrange
        RegisterUserDTO dto = new RegisterUserDTO("  New@Example.com  ", "password123");
        String normalizedEmail = "new@example.com";

        when(userRepository.findByEmail(normalizedEmail)).thenReturn(Optional.empty());
        when(passwordHasher.hash(dto.password())).thenReturn("hashed_secret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);

        // Act
        User createdUser = useCase.execute(dto);

        // Assert
        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(normalizedEmail, createdUser.getEmail());
        Assertions.assertEquals("hashed_secret", createdUser.getPasswordHash());

        // Verifica: el UC guardo un User con datos normalizados
        verify(userRepository).save(userCaptor.capture());
        User savedArg = userCaptor.getValue();
        Assertions.assertEquals(normalizedEmail, savedArg.getEmail());
        Assertions.assertEquals("hashed_secret", savedArg.getPasswordHash());

        // Verificaciones finales
        verify(passwordHasher).hash(dto.password());
        verify(activationCodeSender).send(createdUser.getEmail(), createdUser.getActivationCode());
    }

    @Test
    void testExecute_EmailAlreadyExists() {
        // Arrange
        RegisterUserDTO dto = new RegisterUserDTO("exist@example.com", "password123");

        // Se encuentra un usuario
        when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(Mockito.mock(User.class)));

        // Act & Assert
        Assertions.assertThrows(EmailAlreadyExistsException.class, () -> {
            useCase.execute(dto);
        });

        // Verificamos que no se haya guardado
        verify(userRepository, Mockito.never()).save(any());
        verify(activationCodeSender, Mockito.never()).send(any(), any());
        verify(passwordHasher, Mockito.never()).hash(any());
    }

    @Test
    void testExecute_InvalidEmail_ThrowsInvalidEmailException() {
        RegisterUserDTO dto = new RegisterUserDTO("   ", "password123");
        Assertions.assertThrows(InvalidEmailException.class, () -> useCase.execute(dto));
        verify(userRepository, Mockito.never()).findByEmail(any());
        verify(userRepository, Mockito.never()).save(any());
        verify(passwordHasher, Mockito.never()).hash(any());
        verify(activationCodeSender, Mockito.never()).send(any(), any());
    }

    @Test
    void testExecute_InvalidPassword_ThrowsInvalidPasswordException() {
        RegisterUserDTO dto = new RegisterUserDTO("test@example.com", "123");
        Assertions.assertThrows(InvalidPasswordException.class, () -> useCase.execute(dto));
        verify(userRepository, Mockito.never()).findByEmail(any());
        verify(userRepository, Mockito.never()).save(any());
        verify(passwordHasher, Mockito.never()).hash(any());
        verify(activationCodeSender, Mockito.never()).send(any(), any());
    }

    @Test
    void testExecute_ExpiryIsNowPlus30Minutes() {
        RegisterUserDTO dto = new RegisterUserDTO("new@example.com", "password123");

        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordHasher.hash(dto.password())).thenReturn("hashed_secret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = useCase.execute(dto);

        LocalDateTime now = LocalDateTime.now(fixedClock);
        Assertions.assertEquals(now.plusMinutes(30), created.getActivationExpiresAt());
    }
}