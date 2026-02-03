package com.ramaccioni.api_clean_arch.adapter.persistence;

import com.ramaccioni.api_clean_arch.adapter.persistence.jpa.ISpringDataUserRepository;
import com.ramaccioni.api_clean_arch.core.model.User;
import com.ramaccioni.api_clean_arch.core.output.IUserRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class JpaUserRepositoryAdapter implements IUserRepository {

    private final ISpringDataUserRepository jpa;

    public JpaUserRepositoryAdapter(ISpringDataUserRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpa.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return jpa.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpa.findById(id);
    }

    @Override
    @Transactional
    public int expirePendingUsers(LocalDateTime now) {
        return jpa.expirePendingUsers(now);
    }
}
