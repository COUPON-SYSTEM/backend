package com.company.demo.giftcoupon.exception;

import com.company.demo.common.response.error.ErrorCode;
import com.company.demo.common.response.exception.BusinessException;

public class CouponIssueException extends BusinessException {
  public CouponIssueException(ErrorCode errorCode) {
    super(errorCode);
  }
}
