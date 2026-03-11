package com.company.demo.giftcoupon.batch.exception;

import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.common.response.exception.BusinessException;

public class SkipDataException extends BusinessException {
    public SkipDataException(ErrorCode errorCode) {
        super(errorCode);
    }
}
