package com.company.demo.giftcoupon.batch.exception;

import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.common.response.exception.BusinessException;

public class RetryInfraException extends BusinessException {
    public RetryInfraException(ErrorCode errorCode) {
        super(errorCode);
    }
}
