package com.company.demo.common.response.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    UNEXPECTED(-1, "-1", "Unexpected exception occurred"),
    INVALID_INPUT_VALUE(400, "C004", "Invalid Input Value"),
    METHOD_NOT_ALLOWED(405, "C005", "Method not allowed"),

    NOTIFICATION_SEND_FAILED(500, "C006", "[ERROR] 사용자 알림 전송에 실패했습니다."),
    SSE_SEND_FAILED(500, "C006", "[ERROR] SSE 전송과 연결에 실패했습니다"),
    COUPON_ISSUANCE_CLOSED(429, "C006", "Coupon Issuance is closed"),
    COUPON_REDIS_FAILED(500, "C007", "Coupon Issuance Redis result is invalid"),
    REDIS_CONNECTION_FAILED(501, "C008", "Redis connection failed"),
    COUPON_SERIALIZATION_FAILED(500, "C009", "Outbox event failed serialization"),
    DUPLICATE_USER_REQUEST(502, "C010", "User is duplicated"),
    NOT_FOUND_FCMTOKEN(404, "C011", "[ERROR] 사용자의 FCMTOKEN을 확인할 수 없습니다." ),
    NOT_FOUND_USER(404, "C012", "[ERROR] 아이디에 해당하는 사용자를 찾을 수 없습니다."),
    INVALID_COUPON_ID(400, "C013","[ERROR] 아이디에 해당하는 쿠폰을 찾을 수 없습니다."),
    INVALID_FORMAT(400, "C01","유효하지 않은 데이터 포맷입니다."),
    BUSINESS_RULE(400, "C013","중복발급/유니크)"),
    REDIS_TEMPORARY(400, "C013","일시적인 레디스 오류입니다."),
    DB_CONNECTION_FAILED(400, "C013","일시적인 데이터 베이스 오류입니다."),
    OUTBOX_TEMPORARY(400, "C013","아웃박스 테이블에서 발생한 오류입니다."),
    DB_WRITE_FAILED(500, "C015", "DB write failed");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
