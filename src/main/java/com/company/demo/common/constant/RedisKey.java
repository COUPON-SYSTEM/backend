package com.company.demo.common.constant;

public final class RedisKey {

    private static final String COUPON_TAG = "coupon";

    public static final String COUPON_REQUEST_QUEUE_KEY =
            COUPON_TAG + ":request:queue";

    public static final String COUPON_ISSUED_SET_KEY =
            COUPON_TAG + ":issued";

    public static final String COUPON_TOTAL_COUNT_KEY =
            COUPON_TAG + ":request:count";

    public static final String COUPON_USER_GUARD_PREFIX =
            COUPON_TAG + ":user:";

    private RedisKey() {}
}
