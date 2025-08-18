package com.company.demo.common.response.error;

import com.company.demo.common.response.ApiResponse;
import com.company.demo.common.response.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
public class GlobalExceptionHandler {
    private ResponseEntity<ApiResponse<?>> newResponseEntity(String code, String message, HttpStatus status) {
        return ResponseEntity.status(status).body(ApiResponse.error(code, message));
    }

    @ExceptionHandler(BusinessException.class) // BusinessException를 상속받은 예외처리
    public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException e) {
        log.error("businessException exception occurred: {}", e.getMessage(), e);

        return newResponseEntity(e.getErrorCode().getCode(), e.getMessage(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({Exception.class, RuntimeException.class}) // 정의하지 않은 예외처리
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unexpected exception occurred: {}", e.getMessage(), e);

        return newResponseEntity(ErrorCode.UNEXPECTED.getCode(), e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
