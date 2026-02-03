package com.ramaccioni.api_clean_arch.adapter.jobs;

import com.ramaccioni.api_clean_arch.core.input.IExpirePendingUsersUseCaseInput;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpireUsersJob {

    private final IExpirePendingUsersUseCaseInput useCase;

    public ExpireUsersJob(IExpirePendingUsersUseCaseInput useCase) {
        this.useCase = useCase;
    }

    @Scheduled(fixedDelayString = "${app.jobs.expire-users-delay-ms:60000}")
    public void run() {
        useCase.execute();
    }
}
