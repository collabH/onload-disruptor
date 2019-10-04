package dev.disruptor.pool;

import com.lmax.disruptor.ExceptionHandler;
import dev.disruptor.common.TranslatorDataWapper;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-10-04 11:16
 * @description
 */
public class NettyExceptionHandler implements ExceptionHandler<TranslatorDataWapper> {

    @Override
    public void handleEventException(Throwable ex, long sequence, TranslatorDataWapper event) {

    }

    @Override
    public void handleOnStartException(Throwable ex) {

    }

    @Override
    public void handleOnShutdownException(Throwable ex) {

    }
}
