package io.nugulticket.auth.dto.updateKakaoUserInfo;

import lombok.Getter;

@Getter
public class UpdateKakaoUserInfoRequest {
    private String username;
    private String phoneNumber;
    private String address;

}
