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
    DUPLICATE_USER_REQUEST(502, "C010", "User is duplicated");

    private final int status;
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
