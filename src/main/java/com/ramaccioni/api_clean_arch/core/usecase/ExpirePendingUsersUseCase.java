package com.ramaccioni.api_clean_arch.core.usecase;

import com.ramaccioni.api_clean_arch.core.input.IExpirePendingUsersUseCaseInput;
import com.ramaccioni.api_clean_arch.core.output.IUserRepository;

import java.time.Clock;
import java.time.LocalDateTime;

public class ExpirePendingUsersUseCase implements IExpirePendingUsersUseCaseInput {

    private final IUserRepository userRepository;
    private final Clock clock;

    public ExpirePendingUsersUseCase(IUserRepository userRepository, Clock clock) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    @Override
    public int execute() {
        LocalDateTime now = LocalDateTime.now(clock);
        return userRepository.expirePendingUsers(now);
    }
}
