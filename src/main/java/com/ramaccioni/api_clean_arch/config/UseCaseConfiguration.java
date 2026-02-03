package com.ramaccioni.api_clean_arch.config;

import com.ramaccioni.api_clean_arch.core.input.*;
import com.ramaccioni.api_clean_arch.core.output.*;
import com.ramaccioni.api_clean_arch.core.usecase.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class UseCaseConfiguration {

    @Bean
    public IRegisterUserUseCaseInput registerUserUseCase(
            IUserRepository userRepository,
            IActivationCodeSender activationCodeSender,
            IPasswordHasher passwordHasher,
            @Value("${app.security.activation-expiration-minutes:30}") int expirationMinutes,
            Clock clock
    ) {
        return new RegisterUserUseCase(
                userRepository,
                activationCodeSender,
                passwordHasher,
                expirationMinutes,
                clock
        );
    }

    @Bean
    public IActivateUserUseCaseInput activateUserUseCase(
            IUserRepository userRepository,
            Clock clock
    ) {
        return new ActivateUserUseCase(userRepository, clock);
    }

    @Bean
    public ICreateOrderUseCaseInput createOrderUseCase(
            IUserRepository userRepository,
            IOrderRepository orderRepository,
            Clock clock
    ) {
        return new CreateOrderUseCase(userRepository, orderRepository, clock);
    }

    @Bean
    public IFindOrdersUseCaseInput findOrdersUseCase(IOrderRepository orderRepository) {
        return new FindOrdersUseCase(orderRepository);
    }

    @Bean
    public IExportOrdersUseCaseInput exportOrdersUseCase(
            IOrderRepository orderRepository,
            IOrdersExporter ordersExporter,
            Clock clock
    ) {
        return new ExportOrdersUseCase(orderRepository, ordersExporter, clock);
    }

    @Bean
    public IExpirePendingUsersUseCaseInput expirePendingUsersUseCase(IUserRepository userRepository, Clock clock) {
        return new ExpirePendingUsersUseCase(userRepository, clock);
    }
}
