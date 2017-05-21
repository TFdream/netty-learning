package com.mindflow.netty4.unpack.lengthfield;

import com.mindflow.netty4.common.Constants;
import com.mindflow.netty4.unpack.delimiter.DelimiterClientHandler;
import com.mindflow.netty4.unpack.model.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Ricky Fung
 */
public class NettyClient {

    public void connect(String host, int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new MessageDecoder(1<<20, 10, 4));
                            p.addLast(new MessageEncoder());

                            p.addLast(new ClientHandler());
                        }
                    });

            ChannelFuture future = b.connect(host, port).sync();

            future.awaitUninterruptibly(2000, TimeUnit.MILLISECONDS);
            if(future.channel().isActive()){

                for(int i=0; i<100; i++) {

                    String body = "Hello world from client:"+ i;
                    Message msg = new Message((byte) 0XAF, (byte) 0XBF, i, body);

                    future.channel().writeAndFlush(msg);
                }
            }

            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    private class ClientHandler extends ChannelInboundHandlerAdapter {
        private Logger logger = LoggerFactory.getLogger(getClass());

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {

            logger.info("client read msg:{}, ", msg);

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            logger.error("client caught exception", cause);
            ctx.close();
        }
    }

    public static void main(String[] args) {

        try {
            new NettyClient().connect(Constants.HOST, Constants.PORT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
