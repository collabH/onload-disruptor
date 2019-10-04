package dev.disruptor.server;

import dev.disruptor.common.TranslatorData;
import dev.disruptor.pool.MessageProducer;
import dev.disruptor.pool.RingBufferWorkerPoolFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-10-03 20:19
 * @description 具体数据处理
 * 如果在serverHandler这个netty的线程上处理业务逻辑的时候，会占用netty大量的时间
 */
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       /* TranslatorData request = (TranslatorData) msg;
        log.info("Server端:id:{},name:{},message:{}", request.getId(), request.getName()
                , request.getMessage());
        //数据库持久化操作IO读写---》交给一个线程池 去异步的调用执行
        TranslatorData response = new TranslatorData();
        response.setId("resp:" + request.getId());
        response.setName("resp:" + request.getName());
        response.setMessage("resp:" + request.getMessage());
        //写出去并且刷新到异步nio通道
        ctx.writeAndFlush(response);*/
        TranslatorData request = (TranslatorData) msg;
        String producerId = "disruptor:producer:" + request.getId();
        MessageProducer messageProducer = RingBufferWorkerPoolFactory.getInstance().getMessageProducer(producerId);
        messageProducer.onData(request, ctx);
    }
}
