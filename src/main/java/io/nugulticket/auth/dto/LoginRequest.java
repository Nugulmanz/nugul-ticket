package io.nugulticket.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotNull(message = "이메일을 입력해주세요")
    @Email(message = "이메일 형식을 맞춰주세요.")
    private String email;

    @NotNull(message = "비밀번호를 입력해주세요.")
//    @Pattern(regexp = "^(?=.*?[A-Za-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$",
//            message = "대소문자 포함 영문 + 숫자 + 특수문자를 최소 1글자씩 포함하여 최소 8글자 이상이어야 합니다.")
    private String password;
}
