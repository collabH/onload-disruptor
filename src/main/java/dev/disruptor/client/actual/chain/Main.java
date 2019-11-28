package dev.disruptor.client.actual.chain;

import com.google.common.base.Stopwatch;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-09-26 00:02
 * @description 高级特性入口
 */
@Slf4j
public class Main {
    public static void main(String[] args) throws InterruptedException {


        Stopwatch stopwatch = Stopwatch.createStarted();
        //构建一个线程池用于提交任务
        ExecutorService submitEs = Executors.newFixedThreadPool(4, r -> new Thread(r, "disruptor-executor"));
        CountDownLatch latch = new CountDownLatch(1);
        //1.构建Disruptor
        int ringBufferSize = 1024 * 1024;
        TradeFactory tradeFactory = new TradeFactory();
        /**
         * fixme
         * 如果需要设置五个EventHandler，Disruptor单机需要默认启动5个线程，因为BatchEventProcessor方法内部的run方法，如果有一个handle就需要1个线程
         * 对于单消费者模式来说
         */
        Disruptor<Trade> disruptor = new Disruptor<>(
                tradeFactory, ringBufferSize, new ThreadFactoryBuilder().setNameFormat("disruptor-executor-%d")
                .build(),
                ProducerType.SINGLE, new YieldingWaitStrategy());
        //2 把消费者设置到disruptor中 handleEventsWith
        //2.1 串行操作
//        disruptor.handleEventsWith(new TradeHandleEvent())
//                .handleEventsWith(new TradeHandleEvent1())
//                .handleEventsWith(new TradeHandleEvent2());
        //2.2并行操作
        //disruptor.handleEventsWith(new TradeHandleEvent(), new TradeHandleEvent1(), new TradeHandleEvent2());
        //2.3菱形操作(一)
       /* disruptor.handleEventsWith(new TradeHandleEvent(), new TradeHandleEvent1())
                //串行
                .handleEventsWith(new TradeHandleEvent2());*/
        //2.3菱形操作(二)
//        disruptor.handleEventsWith(new TradeHandleEvent(), new TradeHandleEvent1())
//                .then(new TradeHandleEvent2());
        //2.4多边形操作
        TradeHandleEvent h1 = new TradeHandleEvent();
        TradeHandleEvent1 h2 = new TradeHandleEvent1();
        TradeHandleEvent2 h3 = new TradeHandleEvent2();
        TradeHandleEvent3 h4 = new TradeHandleEvent3();
        TradeHandleEvent4 h5 = new TradeHandleEvent4();
        //并行执行h1 h4
        disruptor.handleEventsWith(h1, h4);
        //h1执行完成后执行h2
        disruptor.after(h1).handleEventsWith(h2);
        //h4执行完成后执行h5
        disruptor.after(h4).handleEventsWith(h5);
        //h2 h5执行完毕后执行h3
        disruptor.after(h2, h5).handleEventsWith(h3);
        //3.启动disruptor
        RingBuffer<Trade> ringBuffer = disruptor.start();
        submitEs.execute(new TradePushLisher(latch, disruptor));
        latch.await();
        disruptor.shutdown();
        submitEs.shutdown();
        stopwatch.stop();
        log.info("cost time:{}", stopwatch.elapsed(TimeUnit.MILLISECONDS));
    }
}

/**
 * Disruptor的Event对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
class Trade {
    private String id;
    private String name;
    private double price;
    private AtomicInteger count = new AtomicInteger(0);
}

class TradeFactory implements EventFactory<Trade> {

    @Override
    public Trade newInstance() {
        return new Trade();
    }
}

/**
 * todo 演示多消费者和串行并行操作
 * WorkHandler另一种方式的消费者，当你不需要long sequence, boolean endOfBatch这些参数时
 */
@Slf4j
class TradeHandleEvent implements EventHandler<Trade>, WorkHandler<Trade> {

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        this.onEvent(event);
    }

    /**
     * 模拟修改名称
     *
     * @param event
     * @throws Exception
     */
    @Override
    public void onEvent(Trade event) throws Exception {
        log.info("TradeHandleEvent: SET NAME");
        Thread.sleep(1000);
        event.setName("H1");

    }
}

/**
 * 消费者2
 */
@Slf4j
class TradeHandleEvent1 implements EventHandler<Trade> {

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        log.info("TradeHandleEvent1:SET ID");
        Thread.sleep(2000);
        event.setId(UUID.randomUUID().toString());

    }
}

@Slf4j
class TradeHandleEvent2 implements EventHandler<Trade> {

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        log.info("TradeHandleEvent2:Name:{},ID:{},INSTANCE:{}", event.getName(), event.getId(), event);
    }
}

@Slf4j
class TradeHandleEvent3 implements EventHandler<Trade> {

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        log.info("TradeHandleEvent3: RESET PRICE");
        event.setPrice(20.0);
    }
}

@Slf4j
class TradeHandleEvent4 implements EventHandler<Trade> {

    @Override
    public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
        log.info("TradeHandleEvent4:GET PRICE:{}", event.getPrice());
        event.setPrice(event.getPrice() + 3.0);
    }
}
