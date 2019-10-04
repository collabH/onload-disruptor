package dev.disruptor.server;

import dev.disruptor.common.TranslatorData;
import dev.disruptor.common.TranslatorDataWapper;
import dev.disruptor.pool.BaseMessageConsumer;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-10-04 11:45
 * @description 服务端消费者实现
 */
@Slf4j
public class MessageConsumerImpl4Server extends BaseMessageConsumer {
    public MessageConsumerImpl4Server(String consumerId) {
        super(consumerId);
    }

    @Override
    public void onEvent(TranslatorDataWapper event) throws Exception {
        TranslatorData request = event.getData();
        log.info("Server端:id:{},name:{},message:{}", request.getId(), request.getName()
                , request.getMessage());

        ChannelHandlerContext ctx = event.getCtx();
        //数据库持久化操作IO读写---》交给一个线程池 去异步的调用执行
        TranslatorData response = new TranslatorData();
        response.setId("resp:" + request.getId());
        response.setName("resp:" + request.getName());
        response.setMessage("resp:" + request.getMessage());
        //写出去并且刷新到异步nio通道
        ctx.writeAndFlush(response);
    }
}
