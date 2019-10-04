package dev.disruptor;

import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import dev.disruptor.pool.BaseMessageConsumer;
import dev.disruptor.pool.RingBufferWorkerPoolFactory;
import dev.disruptor.server.MessageConsumerImpl4Server;
import dev.disruptor.server.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-10-03 19:42
 * @description
 */
@SpringBootApplication
public class NettyServerApp {
    public static void main(String[] args) {
        SpringApplication.run(NettyServerApp.class, args);
        BaseMessageConsumer[] consumers = new BaseMessageConsumer[4];
        for (int i = 0; i < consumers.length; i++) {
            BaseMessageConsumer baseMessageConsumer = new MessageConsumerImpl4Server("disruptor:server:consumer:" + i);
            consumers[i] = baseMessageConsumer;
        }
        RingBufferWorkerPoolFactory.getInstance()
                .initAndStart(ProducerType.MULTI,
                        1024,
                        new YieldingWaitStrategy(),
                        consumers);
        new NettyServer();
    }
}
