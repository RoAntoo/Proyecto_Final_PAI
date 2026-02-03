package com.ramaccioni.api_clean_arch.core.usecase;

import com.ramaccioni.api_clean_arch.core.dto.RegisterUserDTO;
import com.ramaccioni.api_clean_arch.core.exceptions.EmailAlreadyExistsException;
import com.ramaccioni.api_clean_arch.core.exceptions.InvalidEmailException;
import com.ramaccioni.api_clean_arch.core.exceptions.InvalidPasswordException;
import com.ramaccioni.api_clean_arch.core.exceptions.MissingRequiredFieldException;
import com.ramaccioni.api_clean_arch.core.input.IRegisterUserUseCaseInput;
import com.ramaccioni.api_clean_arch.core.model.User;
import com.ramaccioni.api_clean_arch.core.output.IActivationCodeSender;
import com.ramaccioni.api_clean_arch.core.output.IPasswordHasher;
import com.ramaccioni.api_clean_arch.core.output.IUserRepository;

import java.time.Clock;

public class RegisterUserUseCase implements IRegisterUserUseCaseInput {

    private final IUserRepository userRepository;
    private final IActivationCodeSender activationCodeSender;
    private final IPasswordHasher passwordHasher;
    private final int expirationMinutes;
    private final Clock clock;

    public RegisterUserUseCase(
            IUserRepository userRepository,
            IActivationCodeSender activationCodeSender,
            IPasswordHasher passwordHasher,
            int expirationMinutes,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.activationCodeSender = activationCodeSender;
        this.passwordHasher = passwordHasher;
        this.expirationMinutes = expirationMinutes;
        this.clock = clock;
    }

    @Override
    public User execute(RegisterUserDTO dto) {
        // Valida campos requeridos
        if (dto == null || dto.email() == null || dto.password() == null) {
            throw new MissingRequiredFieldException("Email and password are required");
        }

        String email = dto.email().trim().toLowerCase();

        // Valida formato
        if (email.isBlank() || !email.contains("@")) {
            throw new InvalidEmailException("Invalid email format");
        }
        if (dto.password().length() < 8) {
            throw new InvalidPasswordException("Password must be at least 8 characters");
        }

        // Valida que el mail sea unico
        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException(email);
        }

        // PARA LA CONTRASEÑA: haseo usando la interfaz
        String passwordHash = passwordHasher.hash(dto.password());

        User user = User.create(email, passwordHash, expirationMinutes, clock);

        User saved = userRepository.save(user);

        // PARTE DEL SendActivationCode:  envio automatico del codigo luego de que se haya creado el crusiado
        activationCodeSender.send(saved.getEmail(), saved.getActivationCode());

        // Almacena
        return saved;
    }
}