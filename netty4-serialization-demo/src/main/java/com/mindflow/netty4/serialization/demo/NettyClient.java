package com.mindflow.netty4.serialization.demo;

import com.mindflow.netty4.common.Constants;
import com.mindflow.netty4.serialization.model.Request;
import com.mindflow.netty4.serialization.model.Response;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class NettyClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private EventLoopGroup group = new NioEventLoopGroup();

    public void connect(int port, String host) throws Exception {

        // 配置客户端NIO线程组
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast(
                                    new NettyMessageDecoder<Response>(Response.class, 1024 * 1024, 2, 4));
                            ch.pipeline().addLast(new NettyMessageEncoder<Request>(Request.class));
                            ch.pipeline().addLast(new NettyClientHandler());;
                        }
                    });
            // 发起异步连接操作
            ChannelFuture future = b.connect(host, port).sync();

            if (future.awaitUninterruptibly(5000)) {
                logger.info("client connect host:{}, port:{}", host, port);
                if (future.channel().isActive()) {
                    logger.info("开始发送消息");
                    for(int i=0; i<100; i++){

                        Request req = new Request();
                        req.setId((long) i);
                        req.setMessage("hello world");

                        future.channel().writeAndFlush(req);
                    }
                    logger.info("发送消息完毕");
                }
            }

        } finally {

        }
    }

    class NettyClientHandler extends SimpleChannelInboundHandler<Response> {

        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response msg) throws Exception {

            final Response response = msg;
            logger.info("Rpc client receive response id:{}", response.getId());
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.error("捕获异常", cause);
        }
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        new NettyClient().connect(Constants.PORT, Constants.HOST);
    }
}
