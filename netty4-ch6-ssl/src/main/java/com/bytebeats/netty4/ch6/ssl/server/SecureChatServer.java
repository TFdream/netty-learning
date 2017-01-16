package com.bytebeats.netty4.ch6.ssl.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2017-01-16 23:28
 */
public class SecureChatServer {
    private final int port;

    private final String sslMode;

    public SecureChatServer(int port, String sslMode) {
        this.port = port;
        this.sslMode = sslMode;
    }

    public void run() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new SecureChatServerInitializer(sslMode));

            b.bind(port).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Please input ssl mode");
            System.exit(-1);

        }
        String sslMode = args[0];
        int port = 8443;
        new SecureChatServer(port, sslMode).run();
    }
}
