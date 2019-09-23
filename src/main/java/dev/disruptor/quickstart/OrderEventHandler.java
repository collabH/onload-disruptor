package dev.disruptor.quickstart;

import com.lmax.disruptor.EventHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-09-23 23:44
 * @description 实际的消费者对象，负责处理逻辑
 */
@Slf4j
public class OrderEventHandler implements EventHandler<OrderEvent> {
    /**
     * 处理事件
     *
     * @param orderEvent 订单事件
     * @param sequence
     * @param endOfBatch
     * @throws Exception
     */
    @Override
    public void onEvent(OrderEvent orderEvent, long sequence, boolean endOfBatch) throws Exception {
        log.info("orderEvent:{},seq:{},endOfBatch:{}", orderEvent,sequence,endOfBatch);
    }
}
