package dev.disruptor.pool;

import com.lmax.disruptor.WorkHandler;
import dev.disruptor.common.TranslatorDataWapper;
import lombok.Getter;
import lombok.Setter;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-10-04 11:04
 * @description disruptor消费者
 */
public abstract class BaseMessageConsumer implements WorkHandler<TranslatorDataWapper> {
    @Getter
    @Setter
    protected String consumerId;

    public BaseMessageConsumer(String consumerId) {
        this.consumerId = consumerId;
    }
}
