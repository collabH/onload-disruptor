package dev.disruptor.client.actual.multi;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-09-26 23:37
 * @description 多生产多消费模型
 */
@Slf4j
public class Main {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService producerThreadPool = Executors.newFixedThreadPool(100,
                new ThreadFactoryBuilder().setNameFormat("producer-Pool-%d").build());
        //workerPool所需线程池
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
                new ThreadFactoryBuilder().setNameFormat("worker-Pool-%d").build());
        //1.创建ringBuffer容器
        RingBuffer<Order> ringBuffer = RingBuffer.create(ProducerType.SINGLE, Order::new, 1024 * 1024, new YieldingWaitStrategy());
        //2.通过ringBuffer创建ringBuffer屏障
        SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
        //3.构建多消费者数组
        Consumer[] consumers = new Consumer[10];
        for (int i = 0; i < 10; i++) {
            consumers[i] = new Consumer("C" + i);
        }
        //4.构造多消费者工作池
        WorkerPool<Order> workerPool = new WorkerPool<>(ringBuffer,
                sequenceBarrier,
                new EventException(),
                consumers);
        //5.设置多个消费者的sequence序号用于单独统计消费进度，并且设置到ringbuffer中
        ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        //6.启动workerPool
        workerPool.start(executorService);

        CountDownLatch latch = new CountDownLatch(1);
        //多生产者

        for (int i = 0; i < 100; i++) {
            producerThreadPool.execute(() -> {
                Producer1 producer = new Producer1(ringBuffer);
                try {
                    latch.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < 100; j++) {
//                    ringBuffer.publishEvent(producer);
                    producer.sendData(UUID.randomUUID().toString());
                }
            });
        }

        Thread.sleep(3000);
        System.out.println("线程创建完毕，开始生产数据");
        latch.countDown();

        Thread.sleep(4000);
        System.out.println("总条数:"+ consumers[0].getCount());
    }


    /**
     * 事件异常处理类
     */
    static class EventException implements ExceptionHandler<Order> {

        @Override
        public void handleEventException(Throwable ex, long sequence, Order event) {

        }

        @Override
        public void handleOnStartException(Throwable ex) {

        }

        @Override
        public void handleOnShutdownException(Throwable ex) {

        }
    }
}
