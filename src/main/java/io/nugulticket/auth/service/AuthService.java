package io.nugulticket.auth.service;

import io.nugulticket.auth.dto.LoginRequest;
import io.nugulticket.auth.dto.SignoutRequest;
import io.nugulticket.auth.dto.SignupRequest;
import io.nugulticket.auth.dto.SignupResponse;
import io.nugulticket.common.utils.JwtUtil;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.LoginType;
import io.nugulticket.user.enums.UserRole;
import io.nugulticket.user.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {
    private static final String ADMIN_KEY = "adminKey";

    private final BCryptPasswordEncoder passwordEncoders;
    private final JwtUtil jwtUtil;
    private final UserService userService;


    @Transactional
    public SignupResponse createUser(SignupRequest signupRequest) {
        String encodedPassword = passwordEncoders.encode(signupRequest.getPassword());

        if(userService.isUser(signupRequest.getEmail())){
            throw new RuntimeException("해당 유저가 이미 존재합니다.");
        }

        UserRole userRole = Objects.equals(signupRequest.getAdminKey(), ADMIN_KEY) ? UserRole.ADMIN : UserRole.USER;
        User user = new User(
                signupRequest.getEmail(),
                encodedPassword,
                signupRequest.getName(),
                signupRequest.getNickname(),
                signupRequest.getPhoneNumber(),
                userRole,
                LoginType.LOCAL
        );
        User savedUser = userService.addUser(user);
        return SignupResponse.of(savedUser);
    }

    public String login(LoginRequest loginRequest) {
        User user = userService.getUserFromEmail(loginRequest.getEmail());

        if(!passwordEncoders.matches(loginRequest.getPassword(), user.getPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        if(user.getDeletedAt() != null){
            throw new RuntimeException("탈퇴한 사용자 입니다.");
        }

        return jwtUtil.createToken(
                user.getId(),
                user.getEmail(),
                user.getUserRole()
        );
    }

    @Transactional
    public void deleteUser (Long userId) {
        User user = userService.getUser(userId);

        if(user.getDeletedAt() != null){
            throw new RuntimeException("이미 탈퇴한 사용자 입니다.");
        }
        user.deleteAccount();
    }
}
