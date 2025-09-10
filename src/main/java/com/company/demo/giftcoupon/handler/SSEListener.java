package com.company.demo.giftcoupon.handler;

import com.company.demo.giftcoupon.domain.repository.SseEmitterRepository;
import com.company.demo.giftcoupon.event.CouponIssuedEvent;
import com.company.demo.giftcoupon.handler.CouponEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class SSEListener implements CouponEventHandler {

    private SseEmitterRepository sseEmitterRepository;

    @Override
    @Async
    public void handle(CouponIssuedEvent event) {
        pushSSE(event);
    }

    public void pushSSE (CouponIssuedEvent event){

        SseEmitter emitter = sseEmitterRepository.findById(event.getUserId());

        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("couponIssued")
                        .data(event)
                );
                log.info("SSE 데이터 전송 완료");
            } catch (IOException e) {
                log.error("SSE 데이터 전송 실패 - UserId: {}", event.getUserId(), e);
                sseEmitterRepository.deleteById(event.getUserId());
                throw new RuntimeException(e);
            }
        }
    }
}
