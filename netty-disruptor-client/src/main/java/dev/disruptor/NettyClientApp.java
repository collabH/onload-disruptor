package dev.disruptor;

import dev.disruptor.client.NettyClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-10-03 19:41
 * @description
 */
@SpringBootApplication
public class NettyClientApp {
    public static void main(String[] args) {
        SpringApplication.run(NettyClientApp.class, args);
        new NettyClient().sendData();
    }
}
