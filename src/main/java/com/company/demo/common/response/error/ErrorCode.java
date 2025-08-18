package com.company.demo.common.response.error;

import lombok.Getter;

@Getter
public enum ErrorCode {

    UNEXPECTED(-1, "-1", "Unexpected exception occurred"),
    INVALID_INPUT_VALUE(400, "C004", "Invalid Input Value"),
    METHOD_NOT_ALLOWED(405, "C005", "Method not allowed");

    private final int status; // TODO: HTTP Status를 쓸지 고민
    private final String code;
    private final String message;

    ErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
