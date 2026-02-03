package com.ramaccioni.api_clean_arch.config;

import com.ramaccioni.api_clean_arch.core.output.IPasswordHasher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public IPasswordHasher passwordHasher(PasswordEncoder encoder) {
        return encoder::encode; // implementa el puerto usando Spring Security
    }
}
