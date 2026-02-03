package com.ramaccioni.api_clean_arch.core.model;

import com.ramaccioni.api_clean_arch.core.enums.UserStatus;
import com.ramaccioni.api_clean_arch.core.exceptions.ActivationExpiredException;
import com.ramaccioni.api_clean_arch.core.exceptions.InvalidActivationCodeException;
import com.ramaccioni.api_clean_arch.core.exceptions.UserNotPendingException;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import java.time.Clock;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@ToString(exclude = {"passwordHash"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    @Column(nullable = true)
    private String activationCode;

    @Column(nullable = true)
    private LocalDateTime activationExpiresAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;

    public static User create(String email, String passwordHash, int expirationMinutes, Clock clock) {
        LocalDateTime now = LocalDateTime.now(clock);

        User user = new User();
        user.email = email;
        user.passwordHash = passwordHash;
        user.status = UserStatus.PENDING;
        user.createdAt = now;
        user.activationCode = UUID.randomUUID().toString();
        user.activationExpiresAt = now.plusMinutes(expirationMinutes);
        return user;
    }

    public void activate(String code, Clock clock) {
        if (status != UserStatus.PENDING) {
            throw new UserNotPendingException("User is not in PENDING state. Current status: " + status);
        }

        LocalDateTime now = LocalDateTime.now(clock);
        if (activationExpiresAt != null && activationExpiresAt.isBefore(now)) {
            throw new ActivationExpiredException(
                    "Activation code expired. Expired at: " + activationExpiresAt);
        }

        if (!Objects.equals(this.activationCode, code)) {
            throw new InvalidActivationCodeException(
                    "Invalid activation code provided");
        }

        this.status = UserStatus.ACTIVE;
        this.activationCode = null;
        this.activationExpiresAt = null;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}