package dev.disruptor.server;

import dev.disruptor.codec.MarshallingCodeCFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-10-03 19:45
 * @description netty服务端
 */
@Slf4j
public class NettyServer {
    private final static String HOST = "127.0.0.1";
    private final static int PORT = 9999;

    public NettyServer() {
        //1.创建俩个工作线程组:一个用于接受网络请求，另一个用于实际处理业务的线程组
        //TCP中的对应关系:网络连接请求接受的线程组BossGroup相当于Sync队列，处理业务的线程组workGroup相当于accept
        //todo 相当于tcp三次握手中的SYNC队列
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //todo 相当于tcp三次握手中的accept队列
        EventLoopGroup workGroup = new NioEventLoopGroup();

        //辅助类
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    //设置backlog，相当于设置连接数配置
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //表示缓存区动态配置(自适应)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    //缓存区 池化操作
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //处理数据
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            //解码
                            socketChannel.pipeline()
                                    .addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                            //编码
                            socketChannel.pipeline()
                                    .addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                            socketChannel.pipeline()
                                    .addLast(new ServerHandler());
                        }
                    });
            //绑定端口，同步等待请求链接
            ChannelFuture cf = serverBootstrap.bind(HOST, PORT)
                    .sync();
            log.info("Server startup");
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务端绑定异常");
        } finally {
            //优雅关闭
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
