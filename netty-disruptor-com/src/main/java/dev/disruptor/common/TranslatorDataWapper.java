package dev.disruptor.common;

import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.ToString;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-10-03 19:43
 * @description
 */
@Data
@ToString
public class TranslatorDataWapper {
    private TranslatorData data;
    private ChannelHandlerContext ctx;
}
