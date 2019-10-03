package dev.disruptor.server;

import dev.disruptor.common.TranslatorData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-10-03 20:19
 * @description 具体数据处理
 */
@Slf4j
public class ServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TranslatorData request = (TranslatorData) msg;
        log.info("Server端:id:{},name:{},message:{}", request.getId(), request.getName()
                , request.getMessage());
        TranslatorData response = new TranslatorData();
        response.setId("resp:" + request.getId());
        response.setName("resp:" + request.getName());
        response.setMessage("resp:" + request.getMessage());
        //写出去并且刷新到异步nio通道
        ctx.writeAndFlush(response);
    }
}
