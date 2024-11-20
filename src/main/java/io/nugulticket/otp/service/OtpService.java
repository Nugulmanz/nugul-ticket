package io.nugulticket.otp.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.email.service.EmailService;
import io.nugulticket.otp.entity.OtpKey;
import io.nugulticket.otp.repository.OtpKeyRepository;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.UserRole;
import io.nugulticket.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
    private final OtpKeyRepository otpKeyRepository;
    private final OtpRedisService otpRedisService;
    private final EmailService emailService;
    private final UserService userService;

    private static final int MAX_OTP_ATTEMPTS = 5;

    /**
     * QR 코드 생성을 위한 OTP 키 생성 메서드
     *
     * @param authUser 인증된 사용자 정보
     * @return QR 코드 URL (Google Authenticator와 연동할 수 있는 URL)
     */
    public String generateQrCode(AuthUser authUser) {

        Long userId = authUser.getId();

        otpKeyRepository.deleteById(userId);

        GoogleAuthenticatorKey credentials = googleAuthenticator.createCredentials();
        String otpSecretKey = credentials.getKey();

        otpKeyRepository.save(new OtpKey(userId, otpSecretKey, null));

        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("NugulTicket", String.valueOf(userId), credentials);
    }

    /**
     * OTP 검증 메서드
     *
     * @param authUser 인증된 사용자 정보
     * @param otp      사용자가 입력한 OTP 코드
     * @return OTP 검증 성공 여부
     * @throws ApiException 이메일 미인증, OTP 실패, 계정 잠금 등 다양한 에러 상태를 처리
     */
    @Transactional
    public boolean verifyOtp(AuthUser authUser, int otp) {
        Long userId = authUser.getId();
        User user = userService.getUser(userId);

        if (!user.isEmailVerified()) {
            throw new ApiException(ErrorStatus._EMAIL_ALREADY_VERIFIED);
        }

        if (otpRedisService.isAccountLocked(userId)) {
            throw new ApiException(ErrorStatus.OTP_ACCOUNT_LOCKED);
        }

        OtpKey otpKey = otpKeyRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorStatus.OTP_INVALID_VERIFICATION_CODE));

        boolean isValidOtp = googleAuthenticator.authorize(otpKey.getOtpSecretKey(), otp);
        if (!isValidOtp) {
            otpRedisService.incrementFailedOtpAttempts(userId);
            int failedAttempts = otpRedisService.getFailedOtpAttempts(userId);

            if (failedAttempts >= MAX_OTP_ATTEMPTS) {
                otpRedisService.lockAccount(userId);
                String unlockCode = emailService.sendUnlockEmail(user.getEmail());
                otpRedisService.setCodeForUnlock(userId, unlockCode);

                user.changeRole(UserRole.LOCKED);
                userService.updateUserRole(user);

                throw new ApiException(ErrorStatus.OTP_MAX_ATTEMPTS_EXCEEDED);
            }
            throw new ApiException(ErrorStatus.OTP_INVALID_VERIFICATION_CODE);
        }

        otpRedisService.setOtpVerified(userId);
        otpRedisService.resetFailedOtpAttempts(userId);

        user.verifyOtp();
        userService.updateUserRole(user);

        return true;
    }

    /**
     * 계정 잠금을 해제하는 메서드
     *
     * @param authUser 인증된 사용자 정보
     * @param code     사용자가 입력한 잠금 해제 코드
     * @return 잠금 해제 성공 메시지
     * @throws ApiException 잘못된 해제 코드인 경우 예외 처리
     */
    public String unlockAccount(AuthUser authUser, String code) {
        Long userId = authUser.getId();

        if (!otpRedisService.verifyUnlockCode(userId, code)) {
            throw new ApiException(ErrorStatus.OTP_INVALID_UNLOCK_CODE);
        }

        otpRedisService.unlockAccount(userId);

        User user = userService.findById(userId);
        user.changeRole(UserRole.USER);
        userService.updateUserRole(user);

        return "계정 잠금이 해제되었습니다.";
    }

    /**
     * 계정 잠금 해제 코드를 재발송하는 메서드
     *
     * @param authUser 인증된 사용자 정보
     * @return 재발송 성공 메시지
     * @throws ApiException 계정이 잠겨있지 않은 경우 예외 처리
     */
    public String resendUnlockCode(AuthUser authUser) {
        Long userId = authUser.getId();
        User user = userService.getUser(userId);

        if (user.getUserRole() != UserRole.LOCKED) {
            throw new ApiException(ErrorStatus.OTP_ACCOUNT_NOT_LOCKED);
        }

        String email = user.getEmail();
        String code = emailService.sendUnlockEmail(email);
        otpRedisService.setCodeForUnlock(userId, code);

        return "잠금 해제 코드가 이메일로 재발송되었습니다.";
    }

}
