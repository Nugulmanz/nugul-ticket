package io.nugulticket.user.entity;

import io.nugulticket.common.Timestamped;
import io.nugulticket.user.enums.LoginType;
import io.nugulticket.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "users")
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(nullable = false)
    private String username;

    private String nickname;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType;

    @Column(name = "social_id")
    private String socialId;

    @Column(name = "deleted_at", nullable = false)
    private LocalDateTime deletedAt;

}
