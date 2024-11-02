package io.nugulticket.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    ADMIN(Authority.ADMIN),
    SELLER(Authority.SELLER),
    USER(Authority.USER),
    UNVERIFIED_USER(Authority.UNVERIFIED_USER);

    private final String userRole;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 권한입니다"));
    }

    public static class Authority {
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String SELLER = "ROLE_SELLER";
        public static final String USER = "ROLE_USER";
        public static final String UNVERIFIED_USER = "ROLE_UNVERIFIED_USER";
    }
}
