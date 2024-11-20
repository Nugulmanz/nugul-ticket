package io.nugulticket.otp;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.otp.entity.OtpKey;
import io.nugulticket.otp.repository.OtpKeyRepository;
import io.nugulticket.otp.service.OtpRedisService;
import io.nugulticket.otp.service.OtpService;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.LoginType;
import io.nugulticket.user.enums.UserRole;
import io.nugulticket.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceTest {

    @Mock
    private OtpKeyRepository otpKeyRepository;

    @Mock
    private OtpRedisService otpRedisService;

    @Mock
    private UserService userService;

    @InjectMocks
    private OtpService otpService;

    @Test
    void testGenerateQrCode() {
        // Given
        AuthUser authUser = new AuthUser(1L, "test@example.com", UserRole.USER);
        GoogleAuthenticatorKey mockCredentials = new GoogleAuthenticatorKey.Builder("secretKey").build();

        // Mock GoogleAuthenticator 생성
        GoogleAuthenticator googleAuthenticator = mock(GoogleAuthenticator.class);
        ReflectionTestUtils.setField(otpService, "googleAuthenticator", googleAuthenticator);

        when(googleAuthenticator.createCredentials()).thenReturn(mockCredentials);

        // When
        String qrCodeUrl = otpService.generateQrCode(authUser);

        // Then
        assertNotNull(qrCodeUrl);
        verify(otpKeyRepository).deleteById(authUser.getId());
        verify(otpKeyRepository).save(any(OtpKey.class));
    }

    @Test
    void testVerifyOtp_Success() {
        // Given
        AuthUser authUser = new AuthUser(1L, "test@example.com", UserRole.USER);

        User user = mock(User.class);
        when(user.isEmailVerified()).thenReturn(true); // 이메일 인증된 상태만 설정
        OtpKey otpKey = new OtpKey(1L, "secretKey", user);

        when(userService.getUser(authUser.getId())).thenReturn(user);
        when(otpKeyRepository.findById(authUser.getId())).thenReturn(Optional.of(otpKey));
        when(otpRedisService.isAccountLocked(authUser.getId())).thenReturn(false);

        GoogleAuthenticator googleAuthenticator = mock(GoogleAuthenticator.class);
        ReflectionTestUtils.setField(otpService, "googleAuthenticator", googleAuthenticator);
        when(googleAuthenticator.authorize(otpKey.getOtpSecretKey(), 123456)).thenReturn(true);

        // When
        boolean result = otpService.verifyOtp(authUser, 123456);

        // Then
        assertTrue(result);
        verify(otpRedisService).setOtpVerified(authUser.getId());
        verify(userService).updateUserRole(user);
    }



    @Test
    void testVerifyOtp_InvalidOtp() {
        // Given
        AuthUser authUser = new AuthUser(1L, "test@example.com", UserRole.USER);

        // User 객체를 Mocking하여 생성
        User user = mock(User.class);
        lenient().when(user.getId()).thenReturn(1L);
        lenient().when(user.getEmail()).thenReturn("test@example.com");
        lenient().when(user.getUserRole()).thenReturn(UserRole.USER);
        when(user.isEmailVerified()).thenReturn(true);

        // OtpKey 객체 생성
        OtpKey otpKey = new OtpKey(1L, "secretKey", user);

        when(userService.getUser(authUser.getId())).thenReturn(user);
        when(otpKeyRepository.findById(authUser.getId())).thenReturn(Optional.of(otpKey));
        when(otpRedisService.isAccountLocked(authUser.getId())).thenReturn(false);

        // Mock GoogleAuthenticator 인증 실패
        GoogleAuthenticator googleAuthenticator = mock(GoogleAuthenticator.class);
        ReflectionTestUtils.setField(otpService, "googleAuthenticator", googleAuthenticator);
        when(googleAuthenticator.authorize(otpKey.getOtpSecretKey(), 123456)).thenReturn(false);

        // When / Then
        ApiException exception = assertThrows(ApiException.class, () -> otpService.verifyOtp(authUser, 123456));

        // ErrorStatus 검증
        assertEquals(ErrorStatus.OTP_INVALID_VERIFICATION_CODE, exception.getErrorCode());
        verify(otpRedisService).incrementFailedOtpAttempts(authUser.getId());
    }



    @Test
    void testUnlockAccount_Success() {
        // Given
        AuthUser authUser = new AuthUser(1L, "test@example.com", UserRole.LOCKED);
        String unlockCode = "unlock123";

        User user = new User("test@example.com", "password", "username", "nickname", "010-1234-5678", UserRole.LOCKED, LoginType.LOCAL, false);
        ReflectionTestUtils.setField(user, "id", 1L);

        when(otpRedisService.verifyUnlockCode(authUser.getId(), unlockCode)).thenReturn(true);
        when(userService.findById(authUser.getId())).thenReturn(user);

        // When
        String result = otpService.unlockAccount(authUser, unlockCode);

        // Then
        assertEquals("계정 잠금이 해제되었습니다.", result);
        verify(otpRedisService).unlockAccount(authUser.getId());
        verify(userService).updateUserRole(user);
    }





    @Test
    void testUnlockAccount_InvalidCode() {
        // Given
        AuthUser authUser = new AuthUser(1L, "test@example.com", UserRole.LOCKED);
        String unlockCode = "unlock123";

        when(otpRedisService.verifyUnlockCode(authUser.getId(), unlockCode)).thenReturn(false);

        // When / Then
        ApiException exception = assertThrows(ApiException.class, () -> otpService.unlockAccount(authUser, unlockCode));
        assertEquals(ErrorStatus.OTP_INVALID_UNLOCK_CODE, exception.getErrorCode());
    }
}


