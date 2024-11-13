package io.nugulticket.otp.entity;

import io.nugulticket.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@Getter
public class OtpKey {
    @Id
    private Long userId;

    private String otpSecretKey;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    public OtpKey(Long userId, String otpSecretKey, User user) {
        this.userId = userId;
        this.otpSecretKey = otpSecretKey;
        this.user = user;
    }
}