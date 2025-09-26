package com.company.demo.giftcoupon.exception;

import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.common.response.exception.BusinessException;
import lombok.Getter;

@Getter
public class ExternalApiConnectionException extends BusinessException { // 외부 시스템 오류
    public ExternalApiConnectionException(ErrorCode errorCode) {
        super(errorCode);
    }
}
