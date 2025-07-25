package com.company.demo.giftcoupon.batch;

import com.company.demo.common.constant.KafkaTopic;
import com.company.demo.giftcoupon.event.CouponRequestEvent;
import com.company.demo.giftcoupon.producer.CustomKafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaCouponWriter implements ItemWriter<CouponRequestEvent> {

    private final CustomKafkaProducer kafkaProducer;

    @Override
    public void write(Chunk<? extends CouponRequestEvent> items) {
        for (CouponRequestEvent event : items) {
            kafkaProducer.sendRequestMessage(event);
        }
    }
}
