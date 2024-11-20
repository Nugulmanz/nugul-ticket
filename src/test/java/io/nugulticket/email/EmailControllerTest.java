package io.nugulticket.email;

import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.email.controller.EmailController;
import io.nugulticket.email.service.EmailService;
import io.nugulticket.email.service.RedisService;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.LoginType;
import io.nugulticket.user.enums.UserRole;
import io.nugulticket.user.service.UserService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EmailControllerTest {

    @InjectMocks
    private EmailController emailController;

    @Mock
    private EmailService emailService;

    @Mock
    private RedisService redisService;

    @Mock
    private UserService userService;

    @Test
    public void testSendVerificationCode_Success() {
        // Given
        String email = "test@example.com";
        User user = new User("test@example.com", "password", "username", "nickname", "010-1234-5678", UserRole.USER, LoginType.LOCAL, false);
        Mockito.when(userService.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(emailService.joinEmail(email)).thenReturn("123456");

        // When
        String result = emailController.sendVerificationCode(email);

        // Then
        Assertions.assertEquals("인증 코드가 발송되었습니다.", result);
        Mockito.verify(redisService).setCode(email, "123456");
    }

    @Test
    public void testSendVerificationCode_UserNotFound() {
        // Given
        String email = "test@example.com";
        Mockito.when(userService.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            emailController.sendVerificationCode(email);
        });
        Assertions.assertEquals(ErrorStatus._NOT_FOUND_EMAIL, exception.getErrorCode());
    }

    @Test
    public void testVerifyCode_Success() {
        // Given
        String email = "test@example.com";
        String code = "123456";
        String storedCode = "123456";
        User user = new User("test@example.com", "password", "username", "nickname", "010-1234-5678", UserRole.UNVERIFIED_USER, LoginType.LOCAL, true);

        Mockito.when(redisService.getCode(email)).thenReturn(storedCode);
        Mockito.when(userService.findByEmail(email)).thenReturn(Optional.of(user));

        // When
        String result = emailController.verifyCode(email, code);

        // Then
        Assertions.assertEquals("인증이 완료되었습니다.", result);
        Mockito.verify(userService).updateUserRole(user); // 유저의 역할 업데이트 확인
        Assertions.assertTrue(user.isEmailVerified()); // 이메일 인증 상태가 true로 변경되었는지 확인
    }

    @Test
    public void testVerifyCode_InvalidCode() {
        // Given
        String email = "test@example.com";
        String code = "123456";
        String storedCode = "654321"; // 잘못된 코드

        Mockito.when(redisService.getCode(email)).thenReturn(storedCode);

        // When & Then
        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            emailController.verifyCode(email, code);
        });
        Assertions.assertEquals(ErrorStatus._INVALID_VERIFICATION_CODE, exception.getErrorCode());
    }

    @Test
    public void testVerifyCode_UserNotFound() {
        // Given
        String email = "test@example.com";
        String code = "123456";
        String storedCode = "123456";

        Mockito.when(redisService.getCode(email)).thenReturn(storedCode);
        Mockito.when(userService.findByEmail(email)).thenReturn(Optional.empty());

        // When & Then
        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            emailController.verifyCode(email, code);
        });
        Assertions.assertEquals(ErrorStatus._NOT_FOUND_USER, exception.getErrorCode());
    }

    @Test
    public void testVerifyCode_RoleChangeNotAllowed() {
        // Given
        String email = "test@example.com";
        String code = "123456";
        String storedCode = "123456";
        User user = new User("test@example.com", "password", "username", "nickname", "010-1234-5678", UserRole.ADMIN, LoginType.LOCAL, true);

        Mockito.when(redisService.getCode(email)).thenReturn(storedCode);
        Mockito.when(userService.findByEmail(email)).thenReturn(Optional.of(user));

        // When & Then
        ApiException exception = Assertions.assertThrows(ApiException.class, () -> {
            emailController.verifyCode(email, code);
        });
        Assertions.assertEquals(ErrorStatus.ROLE_CHANGE_NOT_ALLOWED, exception.getErrorCode());
    }
}
