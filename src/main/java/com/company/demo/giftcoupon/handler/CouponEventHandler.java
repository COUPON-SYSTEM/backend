package com.company.demo.giftcoupon.handler;

import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.outbox.domain.event.CouponIssuedPayload;
import com.company.demo.giftcoupon.outbox.domain.event.DomainEventEnvelope;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.stereotype.Component;

import java.io.IOException;

public interface CouponEventHandler {
    void handle(DomainEventEnvelope<CouponIssuedPayload> envelope) throws IOException, FirebaseMessagingException;
}
