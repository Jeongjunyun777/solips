package signup.solips.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH-001", "아이디 또는 비밀번호가 잘못되었습니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-002", "유효하지 않은 토큰입니다"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-003", "만료된 토큰입니다"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-004", "유효하지 않은 리프레시 토큰입니다"),

    // 중복 관련 (409 Conflict)
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER-001", "이미 사용 중인 이메일입니다"),
    DUPLICATE_USER_ID(HttpStatus.CONFLICT, "USER-002", "이미 사용 중인 아이디입니다"),

    // Not Found (404)
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-003", "사용자를 찾을 수 없습니다"),

    // Bad Request (400)
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON-001", "입력값이 올바르지 않습니다"),
    MISSING_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON-002", "필수 입력값이 누락되었습니다"),

    // Internal Server Error (500)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-003", "서버 오류가 발생했습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
