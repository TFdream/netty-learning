package com.bytebeats.netty4.sample.ch10;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2016-11-15 00:20
 */
public class AutoReconnectClient {
    private EventLoopGroup workerGroup;
    private Bootstrap b;

    private volatile boolean closed = false;

    private String host;
    private int port;

    public AutoReconnectClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void close() {
        closed = true;
        workerGroup.shutdownGracefully();
        System.out.println("Stopped Tcp Client: " + host);
    }

    public void start(){
        workerGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(workerGroup)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .option(ChannelOption.TCP_NODELAY, true)
        .option(ChannelOption.SO_TIMEOUT, 5000)
        .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline p = ch.pipeline();
                p.addFirst(new ChannelInboundHandlerAdapter() {
                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        super.channelInactive(ctx);
                        ctx.channel().eventLoop().schedule(new Runnable() {
                            @Override
                            public void run() {

                                doConnect();
                            }
                        }, 5, TimeUnit.SECONDS);
                    }
                });
            }
        });

    }

    private void doConnect() {
        if (closed) {
            return;
        }

        ChannelFuture future = b.connect(new InetSocketAddress(host, port));

        future.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture f) throws Exception {
                if (f.isSuccess()) {
                    System.out.println("Started Tcp Client: " + host);
                } else {
                    System.out.println("Started Tcp Client Failed: " + host);
                    f.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {

                            doConnect();
                        }
                    }, 5, TimeUnit.SECONDS);
                }
            }
        });
    }

}
