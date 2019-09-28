package dev.disruptor.actual.multi;

import com.lmax.disruptor.EventTranslator;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-09-27 00:15
 * @description 生产者
 */
@Slf4j
public class Producer implements EventTranslator<Order> {
    @Override
    public void translateTo(Order event, long sequence) {
        event.setId(UUID.randomUUID().toString());

        log.info("sequence：{}", sequence);
    }
}
