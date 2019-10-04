package dev.disruptor.client;

import dev.disruptor.common.TranslatorData;
import dev.disruptor.common.TranslatorDataWapper;
import dev.disruptor.pool.BaseMessageConsumer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-10-04 11:45
 * @description 客户端消费者实现
 */
@Slf4j
public class MessageConsumerImpl4Client extends BaseMessageConsumer {
    public MessageConsumerImpl4Client(String consumerId) {
        super(consumerId);
    }

    @Override
    public void onEvent(TranslatorDataWapper event) throws Exception {
        TranslatorData response = event.getData();
        ChannelHandlerContext ctx = event.getCtx();
        try {
            log.info("response:{}", response);
        } finally {
            ReferenceCountUtil.release(response);
        }

    }
}
