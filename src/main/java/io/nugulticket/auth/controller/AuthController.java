package io.nugulticket.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.nugulticket.auth.dto.LoginRequest;
import io.nugulticket.auth.dto.SignupRequest;
import io.nugulticket.auth.dto.SignupResponse;
import io.nugulticket.auth.dto.kakaoLogin.KakaoLoginResponse;
import io.nugulticket.auth.dto.updateKakaoUserInfo.UpdateKakaoUserInfoRequest;
import io.nugulticket.auth.dto.updateKakaoUserInfo.UpdateKakaoUserInfoResponse;
import io.nugulticket.auth.service.AuthService;
import io.nugulticket.auth.service.KakaoService;
import io.nugulticket.common.AuthUser;
import io.nugulticket.common.apipayload.ApiResponse;
import io.nugulticket.common.apipayload.BaseCode;
import io.nugulticket.common.apipayload.status.ErrorStatus;
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
    public final KakaoService kakaoService;

    @PostMapping("/v1/signup")
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        return ApiResponse.ok(authService.createUser(signupRequest));
    }

    @PostMapping("/v1/signup-admin")
    public ApiResponse<SignupResponse> signupAdmin(@Valid @RequestBody SignupRequest signupRequest) {
        return ApiResponse.ok(authService.createUser(signupRequest));
    }

    @PostMapping("/v1/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).header("Authorization", authService.login(loginRequest)).build();
    }

    @DeleteMapping("/v1/signout/{userId}")
    public ApiResponse<String> deleteUser(@AuthenticationPrincipal AuthUser authUser,
                                           @PathVariable Long userId) {
        // 현재 로그인한 사용자가 탈퇴하려는 계정과 동일한지 확인
        if(!authUser.getId().equals(userId)) {
            return ApiResponse.fail(ErrorStatus._NOT_AUTHENTICATIONPRINCIPAL_USER);
        }
        authService.deleteUser(userId);
        return ApiResponse.ok("탈퇴 되었습니다.");
    }

    @GetMapping("/v1/login/kakao")
    public ApiResponse<KakaoLoginResponse> kakaoLogin (@RequestParam String code) throws JsonProcessingException {
        return ApiResponse.ok(kakaoService.kakaoLogin(code));
    }


    @PutMapping("/v1/login/kakao")
    public ApiResponse<UpdateKakaoUserInfoResponse> updateKakaoUserInfo (@AuthenticationPrincipal AuthUser authUser,
                                                                            @RequestBody UpdateKakaoUserInfoRequest updateKakaoUserInfoRequest) {
        return ApiResponse.ok(kakaoService.updateKakaoUserInfo(authUser, updateKakaoUserInfoRequest));
    }

}
