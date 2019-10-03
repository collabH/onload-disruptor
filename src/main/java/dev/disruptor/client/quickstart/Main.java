package dev.disruptor.client.quickstart;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.ThreadFactory;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-09-23 23:51
 * @description
 */
public class Main {
    public static void main(String[] args) {
        OrderEventFactory orderEventFactory = new OrderEventFactory();
        int ringBufferSize = 4;
        /**
         * 1.orderEventFactory 消息(Event)工厂
         * 2.ringBufferSize 容器的长度
         * 3.ProducerType 分为单生产者和多生产者
         * 4.WaitStrategy 等待策略，
         */
        //1.实例化Disruptor对象
        Disruptor<OrderEvent> disruptor = new Disruptor<>(orderEventFactory,
                ringBufferSize,
                (ThreadFactory) Thread::new,
                ProducerType.SINGLE,
                new BlockingWaitStrategy());
        //2.添加消费者的监听(disruptor与消费者的关联关系)
        OrderEventHandler consumer = new OrderEventHandler();
        disruptor.handleEventsWith(consumer);
        //3.启动disruptor容器
        disruptor.start();

        //4.获取实际存储数据的容器:RingBuffer(环形结构)
        RingBuffer<OrderEvent> ringBuffer = disruptor.getRingBuffer();
        OrderEventProducer producer = new OrderEventProducer(ringBuffer);
        ByteBuffer buffer = ByteBuffer.allocate(8);
        for (long i = 0; i < 9; i++) {
            buffer.putLong(0, i);
            producer.sendData(buffer);
        }
        //5.关闭disruptor
        disruptor.shutdown();
    }
}
