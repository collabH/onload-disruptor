package dev.disruptor;

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
        new NettyServer();
    }
}
