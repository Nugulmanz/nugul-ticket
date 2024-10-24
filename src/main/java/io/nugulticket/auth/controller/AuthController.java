package io.nugulticket.auth.controller;

import io.nugulticket.auth.dto.LoginRequest;
import io.nugulticket.auth.dto.SignupRequest;
import io.nugulticket.auth.dto.SignupResponse;
import io.nugulticket.auth.service.AuthService;
import io.nugulticket.common.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/v1/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(authService.createUser(signupRequest));
    }

    @PostMapping("/v1/signup-admin")
    public ResponseEntity<SignupResponse> signupAdmin(@Valid @RequestBody SignupRequest signupRequest) {
        return ResponseEntity.ok(authService.createUser(signupRequest));
    }

    @PostMapping("/v1/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).header("Authorization", authService.login(loginRequest)).build();
    }

    @DeleteMapping("/v1/signout/{userId}")
    public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal AuthUser authUser,
                                           @PathVariable Long userId) {
        // 현재 로그인한 사용자가 탈퇴하려는 계정과 동일한지 확인
        if(!authUser.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        authService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}
