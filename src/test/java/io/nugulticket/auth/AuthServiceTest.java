package io.nugulticket.auth;

import io.nugulticket.auth.dto.LoginRequest;
import io.nugulticket.auth.dto.SignupRequest;
import io.nugulticket.auth.dto.SignupResponse;
import io.nugulticket.auth.service.AuthService;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.common.utils.JwtUtil;
import io.nugulticket.email.service.EmailService;
import io.nugulticket.email.service.RedisService;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.LoginType;
import io.nugulticket.user.enums.UserRole;
import io.nugulticket.user.service.UserService;
import io.nugulticket.util.TestUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private EmailService emailService;

    @Mock
    private UserService userService;

    @Mock
    private RedisService redisService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Value("sdfsdfsdlkfjskdfj")
    private String ADMIN_KEY;

    @Test
    @DisplayName("일반 사용자 회원가입 성공")
    void createUserAsUserSuccess() {
        // given
        SignupRequest signupRequest = new SignupRequest("a@b.com", "password", "name", "nickname", "010-1234-5678", null);
        User user = TestUtil.getUser();

        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userService.isUser(signupRequest.getEmail())).willReturn(false);
        given(userService.addUser(any(User.class))).willReturn(user);
        given(emailService.joinEmail(anyString())).willReturn("emailVerificationCode");

        // when
        SignupResponse signupResponse = authService.createUser(signupRequest);

        // then
        assertNotNull(signupResponse);
        assertEquals(signupResponse.getEmail(), signupRequest.getEmail());
        verify(userService, times(1)).addUser(any(User.class));
        verify(redisService, times(1)).setCode(signupRequest.getEmail(), "emailVerificationCode");
    }

    @Test
    @DisplayName("중복된 이메일로 회원가입 실패")
    void createUserDuplicateEmailFail() {
        // given
        SignupRequest signupRequest = new SignupRequest("existing@example.com", "password", "name", "nickname", "010-1234-5678", null);

        given(userService.isUser(signupRequest.getEmail())).willReturn(true);

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> authService.createUser(signupRequest));
        assertEquals(ErrorStatus._USER_ALREADY_EXISTS, exception.getErrorCode());
        verify(userService, never()).addUser(any(User.class));
        verify(redisService, never()).setCode(anyString(), anyString());
    }


    @Test
    @DisplayName("잘못된 adminKey로 관리자 회원가입 실패")
    void createUserInvalidAdminKeyFail() {
        // given
        SignupRequest signupRequest = new SignupRequest("admin@example.com", "password", "name", "nickname", "010-1234-5678", "wrongAdminKey");

        given(userService.isUser(signupRequest.getEmail())).willReturn(false);

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> authService.createUser(signupRequest));
        assertEquals(ErrorStatus._INVALID_ADMIN_KEY, exception.getErrorCode());
        verify(userService, never()).addUser(any(User.class));
        verify(redisService, never()).setCode(anyString(), anyString());
    }

    @Test
    @DisplayName("올바른 adminKey로 관리자 회원가입 성공")
    void createUserAsAdminSuccess() {
        // given
        SignupRequest signupRequest = new SignupRequest("admin@example.com", "password", "name", "nickname", "010-1234-5678", ADMIN_KEY);
        User adminUser = new User("admin@example.com", "encodedPassword", "name", "nickname", "010-1234-5678", UserRole.ADMIN, LoginType.LOCAL, false);

        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userService.isUser(signupRequest.getEmail())).willReturn(false);
        given(userService.addUser(any(User.class))).willReturn(adminUser);
        given(emailService.joinEmail(anyString())).willReturn("emailVerificationCode");

        // when
        SignupResponse signupResponse = authService.createUser(signupRequest);

        // then
        assertNotNull(signupResponse);
        assertEquals(signupResponse.getEmail(), signupRequest.getEmail());
        verify(userService, times(1)).addUser(any(User.class));
        verify(redisService, times(1)).setCode(signupRequest.getEmail(), "emailVerificationCode");
    }



    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        // given
        LoginRequest loginRequest = new LoginRequest("user@example.com", "password");
        User user = new User("user@example.com", "encodedPassword", "name", "nickname", "010-1234-5678", UserRole.USER, LoginType.LOCAL, false);

        given(userService.getUserFromEmail(loginRequest.getEmail())).willReturn(user);
        given(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).willReturn(true);
        given(jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole())).willReturn("mockedToken");

        // when
        String token = authService.login(loginRequest);

        // then
        assertNotNull(token);
        assertEquals("mockedToken", token);
        verify(userService, times(1)).getUserFromEmail(loginRequest.getEmail());
        verify(passwordEncoder, times(1)).matches(loginRequest.getPassword(), user.getPassword());
        verify(jwtUtil, times(1)).createToken(user.getId(), user.getEmail(), user.getUserRole());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 실패")
    void loginInvalidPasswordFail() {
        // given
        LoginRequest loginRequest = new LoginRequest("user@example.com", "wrongPassword");
        User user = new User("user@example.com", "encodedPassword", "name", "nickname", "010-1234-5678", UserRole.USER, LoginType.LOCAL, false);

        given(userService.getUserFromEmail(loginRequest.getEmail())).willReturn(user);
        given(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).willReturn(false);

        // when & then
        ApiException exception = assertThrows(ApiException.class, () -> authService.login(loginRequest));
        assertEquals(ErrorStatus._INVALID_PASSWORD, exception.getErrorCode());
        verify(jwtUtil, never()).createToken(anyLong(), anyString(), any(UserRole.class));
    }


}



