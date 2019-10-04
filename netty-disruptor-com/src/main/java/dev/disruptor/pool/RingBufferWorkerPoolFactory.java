package dev.disruptor.pool;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.WorkerPool;
import com.lmax.disruptor.dsl.ProducerType;
import dev.disruptor.common.TranslatorDataWapper;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-10-04 11:00
 * @description ringBuffer工作池工厂
 */
public class RingBufferWorkerPoolFactory {
    private static class SingletonHolder {
        static final RingBufferWorkerPoolFactory instance = new RingBufferWorkerPoolFactory();

    }

    private RingBufferWorkerPoolFactory() {
    }

    public static RingBufferWorkerPoolFactory getInstance() {
        return SingletonHolder.instance;
    }

    private static final Map<String, MessageProducer> producers = Maps.newConcurrentMap();
    private static final Map<String, BaseMessageConsumer> consumers = Maps.newConcurrentMap();

    private RingBuffer<TranslatorDataWapper> ringBuffer;

    private Executor executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 2,
            30,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(1024),
            new ThreadFactoryBuilder().setNameFormat("disruptor-thread-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());


    public void initAndStart(ProducerType type, int bufferSize, WaitStrategy waitStrategy, BaseMessageConsumer[] baseMessageConsumers) {
        //1.构建ringBuffer
        this.ringBuffer = RingBuffer.create(type,
                TranslatorDataWapper::new,
                bufferSize,
                waitStrategy);
        //2.设置barrier，序号栅栏
        SequenceBarrier barrier = this.ringBuffer.newBarrier();
        //3.设置工作池
        WorkerPool<TranslatorDataWapper> workerPool = new WorkerPool<>(this.ringBuffer,
                barrier,
                new NettyExceptionHandler(),
                baseMessageConsumers
        );
        //4.添加到消费者池中
        for (BaseMessageConsumer baseMessageConsumer : baseMessageConsumers) {
            consumers.put(baseMessageConsumer.getConsumerId(), baseMessageConsumer);
        }
        //5.添加sequences
        this.ringBuffer.addGatingSequences(workerPool.getWorkerSequences());
        //6.启动
        workerPool.start(executor);
    }

    public MessageProducer getMessageProducer(String producerId) {
        MessageProducer messageProducer = producers.get(producerId);
        if (null == messageProducer) {
            messageProducer = new MessageProducer(this.ringBuffer, producerId);
            producers.putIfAbsent(producerId, messageProducer);
        }
        return messageProducer;
    }
}
