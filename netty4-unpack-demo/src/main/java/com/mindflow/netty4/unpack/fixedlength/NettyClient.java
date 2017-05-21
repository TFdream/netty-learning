package com.mindflow.netty4.unpack.fixedlength;

import com.mindflow.netty4.common.Constants;
import com.sun.org.apache.xerces.internal.dom.PSVIAttrNSImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ricky Fung
 */
public class NettyClient {

    private Logger logger = LoggerFactory.getLogger(getClass());

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

                            ch.pipeline().addLast(new FixedLengthFrameDecoder(1<<5));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new StringEncoder());

                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });

            ChannelFuture future = b.connect(host, port).sync();

            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    private class ClientHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {

            for(int i=0; i<100; i++){

                String msg = "hello from client "+i;
                logger.info("client send message:{}", msg);

                ctx.writeAndFlush(msg);
            }
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg)
                throws Exception {

            logger.info("接收服务端msg:{}", msg);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
                throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

    }

    public static void main(String[] args) throws InterruptedException {

        new NettyClient().connect(Constants.HOST, Constants.PORT);
    }
}
