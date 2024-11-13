package io.nugulticket.user.dto;

import io.nugulticket.user.enums.LoginType;
import io.nugulticket.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KakaoUserDto {
    private Long id;  //socialId
    private String nickname;
    private String email;
    @Setter
    private UserRole userRole;
    @Setter
    private LoginType loginType;


    public KakaoUserDto(Long id, String nickname, String email) {
        this.id = id;
        this.nickname = nickname;
        this.email = email;
    }
}
