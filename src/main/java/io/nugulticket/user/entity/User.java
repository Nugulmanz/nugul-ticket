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

    private String username;

    private String nickname;

    @Column(name = "phone_number")
    private String phoneNumber;

    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type", nullable = false)
    private LoginType loginType;

    @Column(name = "social_id")
    private Long socialId;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public User(String email, String password, String username, String nickname, String phoneNumber, UserRole userRole, LoginType loginType) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.userRole = userRole;
        this.loginType = loginType;
    }

    public void updateUser(String nickname, String address) {
        this.nickname = nickname;
        this.address = address;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    public void changeUserRole(UserRole userRole) {
        this.userRole = userRole;
    }


    public void deleteAccount(){
        this.deletedAt = LocalDateTime.now();
    }

    public User(String nickname, String kakaoEmail, Long socialId, String encodedPassword, UserRole userRole, LoginType loginType) {
        this.nickname = nickname;
        this.email = kakaoEmail;
        this.socialId = socialId;
        this.password = encodedPassword;
        this.userRole = userRole;
        this.loginType = loginType;
    }

    public User updateSocialIdAndLoginType(Long socialId, LoginType loginType) {
        this.socialId = socialId;
        this.loginType = loginType;
        return this;
    }

    public void updateUserInfo(String username, String phoneNumber, String address) {
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public void becomeSeller(){
        this.userRole = UserRole.SELLER;
    }

}
