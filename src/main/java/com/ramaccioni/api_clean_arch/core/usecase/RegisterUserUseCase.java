package com.ramaccioni.api_clean_arch.core.usecase;

import com.ramaccioni.api_clean_arch.core.exceptions.EmailAlreadyExistsException;
import com.ramaccioni.api_clean_arch.core.dto.RegisterUserDTO;
import com.ramaccioni.api_clean_arch.core.input.IRegisterUserUseCaseInput;
import com.ramaccioni.api_clean_arch.core.model.User;
import com.ramaccioni.api_clean_arch.core.output.IActivationCodeSender;
import com.ramaccioni.api_clean_arch.core.output.IUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;

public class RegisterUserUseCase implements IRegisterUserUseCaseInput {

    private final IUserRepository userRepository;
    private final IActivationCodeSender activationCodeSender;
    private final PasswordEncoder passwordEncoder;          // ← nuevo
    private final int expirationMinutes;
    private final Clock clock;

    public RegisterUserUseCase(
            IUserRepository userRepository,
            IActivationCodeSender activationCodeSender,
            PasswordEncoder passwordEncoder,
            int expirationMinutes,
            Clock clock
    ) {
        this.userRepository = userRepository;
        this.activationCodeSender = activationCodeSender;
        this.passwordEncoder = passwordEncoder;
        this.expirationMinutes = expirationMinutes;
        this.clock = clock;
    }

    @Override
    public User execute(RegisterUserDTO dto) {
        String email = dto.email().trim().toLowerCase();

        if (email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (dto.password().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException(email);
        }

        String passwordHash = passwordEncoder.encode(dto.password());

        User user = User.create(email, passwordHash, expirationMinutes, clock);

        User saved = userRepository.save(user);

        activationCodeSender.send(saved.getEmail(), saved.getActivationCode());

        return saved;
    }

    private UserResponse toResponseDto(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getStatus());
    }
}
