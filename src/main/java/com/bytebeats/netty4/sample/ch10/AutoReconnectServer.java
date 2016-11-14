package com.bytebeats.netty4.sample.ch10;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.concurrent.TimeUnit;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2016-11-15 00:09
 */
public class AutoReconnectServer {
    private EventLoopGroup bossGroup;
    private EventLoopGroup  workerGroup;
    private ServerBootstrap b;
    private volatile boolean closed = false;

    private int port;

    public AutoReconnectServer(int port) {
        this.port = port;
    }

    public void start(){
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try{
            b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 512)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {

                }
            });
        }finally {

        }
    }

    public void doBind(){
        if(closed){
            return;
        }
        //连接
        b.bind(port).addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture f) throws Exception {
                if (f.isSuccess()) {
                    System.out.println("Started Tcp Server: " + port);
                } else {
                    System.out.println("Started Tcp Server Failed: " + port);

                    f.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {

                            //3秒后自动重连
                            doBind();
                        }
                    }, 3, TimeUnit.SECONDS);
                }
            }
        });
    }

    public void shutdown(){
        closed = true;
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        System.out.println("Stopped Tcp Server: " + port);
    }
}
