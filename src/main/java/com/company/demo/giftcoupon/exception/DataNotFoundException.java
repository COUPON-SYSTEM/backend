package com.company.demo.giftcoupon.exception;

import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.common.response.exception.BusinessException;
import lombok.Getter;

@Getter
public class DataNotFoundException extends BusinessException { // 데이터 관련 오류
    public DataNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
