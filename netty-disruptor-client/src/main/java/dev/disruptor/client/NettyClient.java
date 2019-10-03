package dev.disruptor.client;

import dev.disruptor.codec.MarshallingCodeCFactory;
import dev.disruptor.common.TranslatorData;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author echo huang
 * @version 1.0
 * @date 2019-10-03 19:45
 * @description netty客户端
 */
@Slf4j
public class NettyClient {
    private final static String HOST = "127.0.0.1";
    private final static int PORT = 9999;
    //扩展完善ConcurrentHashMap<Key,Channel>pool
    private Channel channel;
    //1.创建一个工作线程组:用于实际处理业务的线程组
    //处理业务的线程组workGroup相当于accept
    //todo 相当于tcp三次握手中的accept队列
    private EventLoopGroup workGroup = new NioEventLoopGroup();

    private ChannelFuture cf;

    public NettyClient() {
        this.connect(HOST, PORT);
    }

    private void connect(String host, int port) {


        //辅助类(注意Client和Server不一样)
        Bootstrap bootstrap = new Bootstrap();
        try {
            bootstrap.group(workGroup)
                    .channel(NioSocketChannel.class)
                    //设置backlog，相当于设置连接数配置
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    //表示缓存区动态配置(自适应)
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    //缓存区 池化操作
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    //处理数据
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                            sc.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                            sc.pipeline().addLast(new ClientHandler());
                        }
                    });
            //绑定端口，同步等待请求链接
            this.cf = bootstrap.connect(host, port)
                    .sync();
            log.info("client connected");
            //接下来就进行数据的发送,但是首先我们要获取通道
            this.channel = this.cf.channel();

            //cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("服务端绑定异常");
        }
    }

    /**
     * 发送数据
     */
    public void sendData() {
        for (int i = 0; i < 10; i++) {
            TranslatorData request = new TranslatorData();
            request.setId("" + i);
            request.setName("hsm" + i);
            request.setMessage("msg" + i);
            this.channel.writeAndFlush(request);
        }
    }

    public void close() throws InterruptedException {
        this.cf.channel().closeFuture().sync();
        //优雅关闭
        workGroup.shutdownGracefully();
    }
}
