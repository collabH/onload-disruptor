package dev.disruptor.juc;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-09-28 17:08
 * @description
 */
public class Review {
    public static void main(String[] args) {
        Exchanger<String> abc = new Exchanger<>();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 10, 0, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new ThreadFactoryBuilder()
                        .setNameFormat("test-queue-%d")
                        .build(), new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
