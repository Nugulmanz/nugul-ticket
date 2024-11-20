package io.nugulticket.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum LoginType {

    LOCAL, SOCIAL;

    public static LoginType of(String type) {
        return Arrays.stream(LoginType.values())
                .filter(t -> t.name().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid LoginType: " + type));
    }
}
