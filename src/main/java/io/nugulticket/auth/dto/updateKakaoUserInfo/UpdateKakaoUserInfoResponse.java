package io.nugulticket.auth.dto.updateKakaoUserInfo;

import io.nugulticket.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateKakaoUserInfoResponse {
    private Long id;
    private String email;
    private String username;
    private String nickname;
    private String phoneNumber;
    private String address;

    public static UpdateKakaoUserInfoResponse of(User user) {
        return new UpdateKakaoUserInfoResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getNickname(),
                user.getPhoneNumber(),
                user.getAddress()
        );
    }

}
