package io.nugulticket.common.exception;

import io.nugulticket.common.apipayload.BaseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiException extends RuntimeException {
    private final BaseCode errorCode;
}
