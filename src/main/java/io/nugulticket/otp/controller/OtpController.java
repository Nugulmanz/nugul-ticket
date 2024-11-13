package io.nugulticket.otp.controller;

import io.nugulticket.common.AuthUser;
import io.nugulticket.otp.service.OtpService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/otp")
public class OtpController {
    private final OtpService otpService;

    @GetMapping("/generate-qr")
    public String generateQrCode(@AuthenticationPrincipal AuthUser authUser) {

        return otpService.generateQrCode(authUser);
    }

    @PostMapping("/verify")
    public String verifyOtp(@AuthenticationPrincipal AuthUser authUser, @RequestParam int otp) {
        boolean isVerified = otpService.verifyOtp(authUser, otp);
        return isVerified ? "OTP 인증 성공" : "OTP 인증 실패";
    }

    @PostMapping("/unlock")
    public String unlockAccount(@AuthenticationPrincipal AuthUser authUser, @RequestParam String code) {
        return otpService.unlockAccount(authUser, code);
    }

    @PostMapping("/resend-unlock-code")
    public String resendUnlockCode(@AuthenticationPrincipal AuthUser authUser) {
        return otpService.resendUnlockCode(authUser);
    }
}