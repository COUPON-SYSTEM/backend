package com.company.demo.common.constant;

public class RedisKey {

    private static final String COUPON_QUEUE_KEY = "coupon:queue";
    private static final String COUPON_ISSUED_SET_KEY = "coupon:issued";

    private RedisKey() {} // 인스턴스화 방지

}
