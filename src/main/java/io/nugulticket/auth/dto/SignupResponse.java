package io.nugulticket.auth.dto;

import io.nugulticket.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SignupResponse {
    private final Long id;
    private final String email;
    private final String name;
    private final LocalDateTime createdAt;

    public static SignupResponse of(User user){
        return new SignupResponse(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getCreatedAt()
        );
    }
}
