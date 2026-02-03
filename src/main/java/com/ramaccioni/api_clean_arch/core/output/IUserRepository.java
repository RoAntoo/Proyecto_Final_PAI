package com.ramaccioni.api_clean_arch.core.output;

import com.ramaccioni.api_clean_arch.core.model.User;

import java.util.Optional;

public interface IUserRepository {
    Optional<User> findByEmail(String email);
    User save(User user);
    Optional<User> findById(Long id);

    int expirePendingUsers(java.time.LocalDateTime now);
}
