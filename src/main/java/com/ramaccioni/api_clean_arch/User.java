package com.ramaccioni.api_clean_arch;

import com.ramaccioni.api_clean_arch.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

    private String activationCode;

    private LocalDateTime activationExpiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    //Factura
    public static User create(String email, String password, int expirationMinutes) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.status = UserStatus.PENDING;
        user.createdAt = LocalDateTime.now();

        user.activationCode = UUID.randomUUID().toString();
        user.activationExpiresAt = LocalDateTime.now().plusMinutes(expirationMinutes);

        return user;
    }
}
