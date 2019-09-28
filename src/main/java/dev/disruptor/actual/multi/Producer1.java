package dev.disruptor.actual.multi;

import com.lmax.disruptor.RingBuffer;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-09-27 01:01
 * @description
 */
public class Producer1 {
    private RingBuffer<Order> ringBuffer;

    public Producer1(RingBuffer<Order> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void sendData(String uuid) {
        long sequence = ringBuffer.next();
        try {
            Order order = ringBuffer.get(sequence);
            order.setId(uuid);
        } finally {
            ringBuffer.publish(sequence);
        }
    }
}
