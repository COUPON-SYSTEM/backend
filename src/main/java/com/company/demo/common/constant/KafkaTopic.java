package com.company.demo.common.constant;

public final class KafkaTopic {

    public static final String GIFT_REQUEST = "gift-request";
    public static final String ORDER_CREATED = "order-created";
    public static final String COUPON_ISSUED = "coupon-issued";
    public static final String TEST_TOPIC = "test-topic";

    private KafkaTopic() {} // 인스턴스화 방지
}
