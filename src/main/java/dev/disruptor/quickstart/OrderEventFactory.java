package dev.disruptor.quickstart;

import com.lmax.disruptor.EventFactory;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-09-23 23:42
 * @description 工厂类
 */
public class OrderEventFactory implements EventFactory<OrderEvent> {

    @Override
    public OrderEvent newInstance() {
        //这个方法为类返回空的数据对象(Event)
        return new OrderEvent();
    }
}
