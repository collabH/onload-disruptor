package dev.disruptor.client;

import dev.disruptor.common.TranslatorData;
import dev.disruptor.pool.MessageProducer;
import dev.disruptor.pool.RingBufferWorkerPoolFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-10-03 21:42
 * @description 客户端处理器
 */
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
     /*   try {
            TranslatorData response = (TranslatorData) msg;
            log.info("response:{}", response);
        } finally {
            //一定要注意 用完了缓存 要进行释放
            ReferenceCountUtil.release(msg);
        }*/
        TranslatorData response = (TranslatorData) msg;
        String producerId = "disruptor:producer:" + response.getId();
        MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);
        messageProducer.onData(response, ctx);
    }
}
