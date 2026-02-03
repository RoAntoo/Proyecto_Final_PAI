package com.ramaccioni.api_clean_arch.adapter.persistence.jpa;

import com.ramaccioni.api_clean_arch.core.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ISpringDataUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    @Modifying
    @Query("""
        update User u
           set u.status = com.ramaccioni.api_clean_arch.core.enums.UserStatus.EXPIRED,
               u.activationCode = null,
               u.activationExpiresAt = null
         where u.status = com.ramaccioni.api_clean_arch.core.enums.UserStatus.PENDING
           and u.activationExpiresAt is not null
           and u.activationExpiresAt < :now
    """)
    int expirePendingUsers(@Param("now") LocalDateTime now);
}
