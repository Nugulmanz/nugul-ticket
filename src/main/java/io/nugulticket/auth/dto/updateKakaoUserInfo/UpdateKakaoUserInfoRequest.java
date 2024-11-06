package io.nugulticket.auth.dto.updateKakaoUserInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateKakaoUserInfoRequest {
    private String username;
    private String phoneNumber;
    private String address;

}
