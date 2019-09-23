package dev.disruptor.compare;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import org.junit.Test;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-09-22 23:06
 * @description Disruptor和BlockingQueue对比
 */
public class DisruptorVBlockingQueue {

    /**
     * 测试jdk阻塞队列耗时 单位ms
     * 1亿数据 19981
     * 5千万数据 9855
     * 1千万数据 2400
     */
    @Test
    public void testQueueCost() {
        final ArrayBlockingQueue<TestData> queue = new ArrayBlockingQueue<>(100000000);
        long startTime = System.currentTimeMillis();
        try {
            new Thread(() -> {
                long i = 0;

                while (i < 100000000) {
                    TestData data = new TestData(i, "hsm" + i);
                    try {
                        queue.put(data);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
            }).start();

            new Thread(() -> {
                long i = 0;

                while (i < 100000000) {
                    try {
                        TestData poll = queue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    i++;
                }
                //2293ms
                System.out.println(System.currentTimeMillis() - startTime);
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 1亿数据 10361
     * 5千万数据 5956
     * 1千万数据 1322
     */
    @Test
    public void testDisruptor() {
        int ringBufferSize = 65536;
        Disruptor<TestData> disruptor = new Disruptor<>(TestData::new, ringBufferSize, Executors.newSingleThreadExecutor(), ProducerType.SINGLE, new YieldingWaitStrategy());
        TestDataConsume consume = new TestDataConsume();
        disruptor.handleEventsWith(consume);
        disruptor.start();
        new Thread(() -> {
            RingBuffer<TestData> ringBuffer = disruptor.getRingBuffer();
            for (long i = 0; i < 50000000; i++) {
                long seq = ringBuffer.next();
                TestData data = ringBuffer.get(seq);
                data.setCount(i);
                data.setName("hsm" + i);
                ringBuffer.publish(seq);
            }
        }).start();
    }
}

