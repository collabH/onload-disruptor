package dev.disruptor.actual;

import com.google.common.base.Stopwatch;
import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;
import java.util.concurrent.*;
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
        Disruptor<Trade> disruptor = new Disruptor<>(
                tradeFactory, ringBufferSize, (ThreadFactory) Thread::new,
                ProducerType.SINGLE, new YieldingWaitStrategy());
        //2 把消费者设置到disruptor中 handleEventsWith
        //2.1 串行操作
//        disruptor.handleEventsWith(new TradeHandleEvent())
//                .handleEventsWith(new TradeHandleEvent1())
//                .handleEventsWith(new TradeHandleEvent2());
        //2.2并行操作
        disruptor.handleEventsWith(new TradeHandleEvent(), new TradeHandleEvent1(), new TradeHandleEvent2());
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
