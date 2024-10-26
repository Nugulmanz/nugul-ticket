package io.nugulticket.user.dto.authorityAccept;

import io.nugulticket.user.enums.UserRole;
import lombok.Getter;

@Getter
public class AuthorityAcceptResponse {
    private long userId;
    private UserRole userRole;

    public AuthorityAcceptResponse(long userId, UserRole userRole) {
        this.userId = userId;
        this.userRole = userRole;
    }
}
