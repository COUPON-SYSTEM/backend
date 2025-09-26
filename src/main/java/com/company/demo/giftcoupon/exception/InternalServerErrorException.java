package com.company.demo.giftcoupon.exception;

import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.common.response.exception.BusinessException;
import lombok.Getter;

@Getter
public class InternalServerErrorException extends BusinessException { // 내부 시스템 오류
    public InternalServerErrorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
