package io.nugulticket.email.controller;


import io.nugulticket.common.apipayload.status.ErrorStatus;
import io.nugulticket.common.exception.ApiException;
import io.nugulticket.email.service.EmailService;
import io.nugulticket.email.service.RedisService;
import io.nugulticket.user.entity.User;
import io.nugulticket.user.enums.UserRole;
import io.nugulticket.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;
    private final RedisService redisService;
    private final UserService userService;

    @PostMapping("/send-code")
    public String sendVerificationCode(@RequestParam String email) {

        Optional<User> user = userService.findByEmail(email);
        if (user.isEmpty()) {
            throw new ApiException(ErrorStatus._NOT_FOUND_EMAIL);
        }

        String code = emailService.joinEmail(email);
        redisService.setCode(email, code);
        return "인증 코드가 발송되었습니다.";
    }

    @PreAuthorize("hasAuthority('UNVERIFIED_USER')")
    @Transactional
    @PostMapping("/verify-code")
    public String verifyCode(@RequestParam String email, @RequestParam String code) {
        String storedCode = redisService.getCode(email);
        if (!storedCode.equals(code)) {
            throw new ApiException(ErrorStatus._INVALID_VERIFICATION_CODE);
        }

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new ApiException(ErrorStatus._NOT_FOUND_USER));
        user.verifyEmail();

        if (user.getUserRole() == UserRole.UNVERIFIED_USER) {
            user.changeRole(UserRole.USER);
            userService.updateUserRole(user);
        } else {
            throw new ApiException(ErrorStatus.ROLE_CHANGE_NOT_ALLOWED);
        }


        return "인증이 완료되었습니다.";
    }
}
