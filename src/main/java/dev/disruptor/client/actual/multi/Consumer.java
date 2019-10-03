package dev.disruptor.client.actual.multi;

import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-09-26 23:42
 * @description 消费者
 */
@Slf4j
public class Consumer implements WorkHandler<Order> {
    private String comsumerId;
    private static AtomicInteger COUNT = new AtomicInteger(0);

    public Consumer(String comsumerId) {
        this.comsumerId = comsumerId;
    }

    @Override
    public void onEvent(Order event) throws Exception {

        Thread.sleep(RandomUtils.nextInt(1, 5));
        log.info("当前消费者:{},消费信息ID:{}", this.comsumerId, event.getId());
        COUNT.incrementAndGet();
    }

    public int getCount() {
        return COUNT.get();
    }
}
