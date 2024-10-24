package io.nugulticket.auth.dto.kakaoLogin;

import lombok.Getter;

@Getter
public class KakaoLoginResponse {
    String bearerToken;

    public KakaoLoginResponse(String token) {
        this.bearerToken = token;
    }
}
