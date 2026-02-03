package com.ramaccioni.api_clean_arch.core.usecase;

import com.ramaccioni.api_clean_arch.core.dto.ActivateUserDTO;
import com.ramaccioni.api_clean_arch.core.exceptions.MissingRequiredFieldException;
import com.ramaccioni.api_clean_arch.core.exceptions.UserAlreadyActiveException;
import com.ramaccioni.api_clean_arch.core.exceptions.UserNotFoundException;
import com.ramaccioni.api_clean_arch.core.input.IActivateUserUseCaseInput;
import com.ramaccioni.api_clean_arch.core.model.User;
import com.ramaccioni.api_clean_arch.core.output.IUserRepository;

import java.time.Clock;

public class ActivateUserUseCase implements IActivateUserUseCaseInput {
    private final IUserRepository userRepository;
    private final Clock clock;

    public ActivateUserUseCase(IUserRepository userRepository, Clock clock) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Override
    public void execute(ActivateUserDTO dto) {

        // Valida campos requeridos
        if (dto == null || dto.userId() == null || dto.activationCode() == null) {
            throw new MissingRequiredFieldException("userId and activationCode are required");
        }

        // Busca el usuario
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + dto.userId()));

        if (user.isActive()) {
            throw new UserAlreadyActiveException("User is already active");
        }

        user.activate(dto.activationCode().trim(), clock);

        // Almacena
        userRepository.save(user);
    }
}
