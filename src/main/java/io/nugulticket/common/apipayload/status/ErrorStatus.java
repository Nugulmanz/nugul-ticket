package io.nugulticket.common.apipayload.status;

import io.nugulticket.common.apipayload.BaseCode;
import io.nugulticket.common.apipayload.dto.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseCode {

    //Auth
    _NOT_AUTHENTICATIONPRINCIPAL_USER(HttpStatus.UNAUTHORIZED, "401", "인증되지 않은 유저입니다."),
    _USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "409", "해당 유저가 이미 존재합니다."),
    _INVALID_ADMIN_KEY(HttpStatus.FORBIDDEN, "403", "adminKey가 일치하지 않습니다."),
    _INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "401", "비밀번호가 일치하지 않습니다."),
    _USER_ALREADY_DELETED(HttpStatus.FORBIDDEN, "403", "탈퇴한 사용자 입니다."),

    // common
    _PERMISSION_DENIED(HttpStatus.FORBIDDEN, "403", "권한이 없습니다."),

    // auction
    _Lower_Than_Current_Bid(HttpStatus.BAD_REQUEST, "400", "최근 입찰가 보다 높아야 합니다."),
    _EXPIRED_ACTION(HttpStatus.BAD_REQUEST, "400", "종료된 경매입니다."),
    _NOT_FOUND_AUCTION(HttpStatus.NOT_FOUND,"404", "존재하지 않는 경매입니다."),

    // ticket
    _ALREADY_RESERVED(HttpStatus.NOT_ACCEPTABLE,"406", "이미 예약된 좌석입니다."),
    _NOT_FOUND_TICKET(HttpStatus.NOT_FOUND, "404", "존재하지 않는 티켓입니다"),
    _CANT_TRANSFER_STATE(HttpStatus.BAD_REQUEST, "406", "티켓 양도가 불가능한 상태 입니다"),

    // user
    _NOT_FOUND_USER(HttpStatus.NOT_FOUND, "404", "존재하지 않는 유저입니다"),
    _FORBIDDEN_USER(HttpStatus.FORBIDDEN,"403", "어드민 유저만 접근할 수 있습니다."),
    _FORBIDDEN_ROLE(HttpStatus.FORBIDDEN,"403", "접근할 수 없는 권한입니다."),
    ROLE_CHANGE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "403", "UNVERIFIED_USER 상태에서만 역할 변경이 가능합니다."),

    // event
    SELLER_ROLE_REQUIRED(HttpStatus.FORBIDDEN, "403", "SELLER 권한이 필요합니다."),
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "404","해당 공연을 찾을 수 없습니다."),
    ADMIN_ROLE_REQUIRED(HttpStatus.FORBIDDEN, "403", "ADMIN 권한이 필요합니다."),

    // image
    _FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "500", "이미지 업로드에 실패하였습니다."),
    _EMPTY_FILE(HttpStatus.NOT_FOUND, "404", "이미지 파일이 존재하지 않습니다."),
    _FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "400", "파일 크기가 허용된 제한을 초과했습니다."),
    _UNSUPPORTED_FILE_FORMAT(HttpStatus.BAD_REQUEST, "400", "지원하지 않는 파일 형식입니다."),

    //email
    _NOT_FOUND_EMAIL(HttpStatus.NOT_FOUND, "404", "가입되지 않은 이메일입니다."),
    _INVALID_VERIFICATION_CODE(HttpStatus.BAD_REQUEST, "400", "인증 코드가 올바르지 않습니다."),
    _EMAIL_ALREADY_VERIFIED(HttpStatus.CONFLICT, "409", "이메일이 이미 인증되었습니다."),
    _EMAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "500", "이메일 발송 중 오류가 발생했습니다."),

    //search
    SEARCH_QUERY_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, "500", "검색 쿼리 실행 중 오류가 발생했습니다.");

    private HttpStatus httpStatus;
    private String statusCode;
    private String message;

    @Override
    public ReasonDto getReasonHttpStatus() {
        return ReasonDto.builder()
                .statusCode(statusCode)
                .message(message)
                .httpStatus(httpStatus)
                .success(false)
                .build();
    }
}
