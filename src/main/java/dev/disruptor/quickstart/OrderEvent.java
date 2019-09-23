package dev.disruptor.quickstart;

import lombok.Data;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-09-23 23:41
 * @description 订单事件
 */
@Data
public class OrderEvent {
    private long price;
}
