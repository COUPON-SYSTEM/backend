package com.company.demo.common.constant;

public final class RedisKey {

    public static final String COUPON_REQUEST_QUEUE_KEY = "coupon:request:queue";
    public static final String COUPON_ISSUED_SET_KEY = "coupon:issued";
    public static final String COUPON_REQUEST_COUNT_KEY = "coupon:request:count";

    private RedisKey() {} // 인스턴스화 방지

}
