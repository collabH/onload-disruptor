package dev.disruptor.actual.chain;

import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-09-26 00:17
 * @description
 */
public class TradePushLisher implements Runnable {
    private Disruptor<Trade> disruptor;
    private CountDownLatch latch;
    private final static int PUBLISH_COUNT = 1;

    public TradePushLisher(CountDownLatch latch, Disruptor<Trade> disruptor) {
        this.latch = latch;
        this.disruptor = disruptor;
    }

    @Override
    public void run() {
        TradeEventTranslator tradeEventTranslator = new TradeEventTranslator();
        for (int i = 0; i < PUBLISH_COUNT; i++) {
            //新的提交事件的方式
            disruptor.publishEvent(tradeEventTranslator);
        }
        latch.countDown();
    }
}

/**
 * 事件翻译器
 */
class TradeEventTranslator implements EventTranslator<Trade> {

    /**
     * 快速的传输方式，disruptor中易用的Event投递方式
     *
     * @param event
     * @param sequence
     */
    @Override
    public void translateTo(Trade event, long sequence) {
        this.generateTrade(event);
    }

    private void generateTrade(Trade event) {
        event.setPrice(new Random().nextDouble());
    }
}
