package com.mindflow.netty4.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ricky Fung
 */
public class NettyWsServer {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) throws Exception {
        new NettyWsServer().start(8088);
    }

    public void start(int port) throws InterruptedException {
        // 1.创建对应的EventLoopGroup对象
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try{
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // websocket 相关的配置
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            //因为基于http协议，使用http的编码和解码器
                            pipeline.addLast(new HttpServerCodec());
                            //是以块方式写，添加ChunkedWriteHandler处理器
                            pipeline.addLast(new ChunkedWriteHandler());
                            // http在传输过程中是分段的，这就是为什么当浏览器发送大量数据的时候，会发出多次http请求
                            pipeline.addLast(new HttpObjectAggregator(1024 * 64));

                            // WebSocket数据压缩
                            //pipeline.addLast(new WebSocketServerCompressionHandler());

                            //用户身份鉴权
                            pipeline.addLast(new HttpRequestAuthHandler());

                            //根据websocket规范，处理升级握手以及各种websocket数据帧
                            pipeline.addLast(new WebSocketServerProtocolHandler("/hello", true,10000));

                            // 读空闲60秒激发
                            //pipeline.addLast(new HeartbeatHandler(5, 0, 0, TimeUnit.MINUTES));

                            // 自定义handler，处理业务逻辑
                            pipeline.addLast(new TextWebSocketHandler());

                        }
                    });

            ChannelFuture future = bootstrap.bind(port).sync();
            LOG.info("服务启动了, port={}", port);
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
