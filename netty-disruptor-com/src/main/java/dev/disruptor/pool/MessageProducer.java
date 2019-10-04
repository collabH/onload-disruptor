package dev.disruptor.pool;

import com.lmax.disruptor.RingBuffer;
import dev.disruptor.common.TranslatorData;
import dev.disruptor.common.TranslatorDataWapper;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-10-04 11:04
 * @description disruptor生产者
 */
public class MessageProducer {
    private RingBuffer<TranslatorDataWapper> ringBuffer;
    private String producerId;

    public MessageProducer(RingBuffer<TranslatorDataWapper> ringBuffer, String producerId) {
        this.ringBuffer = ringBuffer;
        this.producerId = producerId;
    }

    public void onData(TranslatorData data, ChannelHandlerContext ctx) {
        long next = ringBuffer.next();
        try {
            TranslatorDataWapper wapper = ringBuffer.get(next);
            wapper.setCtx(ctx);
            wapper.setData(data);
        } finally {
            ringBuffer.publish(next);
        }
    }
}