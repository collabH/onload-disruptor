package dev.disruptor.client.quickstart;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-09-24 00:08
 * @description 订单消息(Event)生产者
 */
public class OrderEventProducer {
    private RingBuffer<OrderEvent> ringBuffer;

    public OrderEventProducer(RingBuffer<OrderEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    /**
     * 投递数据
     *
     * @param data 投递的数据
     */
    public void sendData(ByteBuffer data) {
        //1.在生产者发送消息的时候，首先需要从我们的ringBuffer中获取可用的序号
        long seq = 0;
        try {
            seq = ringBuffer.next();
            //2.根据这个序号找到具体的"OrderEvent"元素
            OrderEvent orderEvent = ringBuffer.get(seq);
            //3.进行实际的赋值处理
            orderEvent.setPrice(data.getLong(0));
        } finally {
            //4.发布操作
            ringBuffer.publish(seq);
        }
    }
}
