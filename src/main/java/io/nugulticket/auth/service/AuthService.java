package io.nugulticket.auth.service;

import io.nugulticket.auth.dto.LoginRequest;
import io.nugulticket.auth.dto.SignupRequest;
import io.nugulticket.auth.dto.SignupResponse;
import io.nugulticket.common.utils.JwtUtil;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.LoginType;
import io.nugulticket.user.enums.UserRole;
import io.nugulticket.user.service.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    @Value("${ADMIN_KEY}")
    private static String ADMIN_KEY; // 관리자 가입 시 사용

    private final BCryptPasswordEncoder passwordEncoders;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * 회원가입 기능(USER, ADMIN 모두 수행)
     * @param signupRequest 회원가입 정보
     * - USER : adminKey = null
     * - ADMIN : adminKey = ADMIN_KEY
     * @return SignupResponse 회원가입 된 사용자 정보
     */
    @Transactional
    public SignupResponse createUser(SignupRequest signupRequest) {
        String encodedPassword = passwordEncoders.encode(signupRequest.getPassword());

        if(userService.isUser(signupRequest.getEmail())){
            throw new RuntimeException("해당 유저가 이미 존재합니다.");
        }

        // 사용자 역할을 설정하기 위한 변수 설정
        UserRole userRole;
        if (signupRequest.getAdminKey().isEmpty()) {
            userRole = UserRole.USER;
        } else if (Objects.equals(signupRequest.getAdminKey(), ADMIN_KEY)) {
            userRole = UserRole.ADMIN;
        } else {
            throw new RuntimeException("adminKey가 일치하지 않습니다.");
        }

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

    /**
     * 로그인 기능
     * @param loginRequest 로그인 정보
     * @return jwt token
     */
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

    /**
     * 회원탈퇴 기능
     * @param userId 탈퇴할 User id
     */
    @Transactional
    public void deleteUser (Long userId) {
        User user = userService.getUser(userId);

        if(user.getDeletedAt() != null){
            throw new RuntimeException("이미 탈퇴한 사용자 입니다.");
        }
        user.deleteAccount();
    }
}
