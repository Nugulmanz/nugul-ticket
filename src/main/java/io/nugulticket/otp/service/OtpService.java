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

    public String generateQrCode(AuthUser authUser) {

        Long userId = authUser.getId();

        otpKeyRepository.deleteById(userId);

        GoogleAuthenticatorKey credentials = googleAuthenticator.createCredentials();
        String otpSecretKey = credentials.getKey();

        otpKeyRepository.save(new OtpKey(userId, otpSecretKey, null));

        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("NugulTicket", String.valueOf(userId), credentials);
    }

    @Transactional
    public boolean verifyOtp(AuthUser authUser, int otp) {
        Long userId = authUser.getId();
        User user = userService.getUser(userId);

        if(!user.isEmailVerified()) {
            throw new ApiException(ErrorStatus._EMAIL_ALREADY_VERIFIED);
        }

        if (!otpRedisService.isOtpVerified(userId)) {
            user.expireOtpVerification();
            userService.updateUserRole(user);
            throw new ApiException(ErrorStatus.OTP_VERIFICATION_REQUIRED);
        }

        if (otpRedisService.isAccountLocked(userId)) {
            throw new ApiException(ErrorStatus.OTP_ACCOUNT_LOCKED);
        }

        OtpKey otpKey = otpKeyRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorStatus.OTP_INVALID_VERIFICATION_CODE));

        boolean isValidOtp = googleAuthenticator.authorize(otpKey.getOtpSecretKey(), otp);

        if (isValidOtp) {
            user.verifyOtp();
            userService.updateUserRole(user);

            otpRedisService.setOtpVerified(userId);
            otpRedisService.resetFailedOtpAttempts(userId);
            return true;
        } else {
            otpRedisService.incrementFailedOtpAttempts(userId);
            int failedAttempts = otpRedisService.getFailedOtpAttempts(userId);

            if (failedAttempts >= MAX_OTP_ATTEMPTS) {
                otpRedisService.lockAccount(userId);

                String code = emailService.sendUnlockEmail(otpKey.getUser().getEmail());
                otpRedisService.setCodeForUnlock(userId, code);

                user.changeRole(UserRole.LOCKED);
                userService.updateUserRole(user);

                throw new ApiException(ErrorStatus.OTP_MAX_ATTEMPTS_EXCEEDED);
            }

            return false;
        }
    }

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
