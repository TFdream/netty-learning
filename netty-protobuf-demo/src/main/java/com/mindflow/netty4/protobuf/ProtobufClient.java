package com.mindflow.netty4.protobuf;

import com.mindflow.netty4.protobuf.model.UserModel;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.net.InetSocketAddress;

/**
 * @author Ricky Fung
 */
public class ProtobufClient {
    
    public static void main(String[] args) throws InterruptedException {
        new ProtobufClient().start("localhost", 8080);
    }
    
    public void start(String host, int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap client = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("encoder", new ProtobufEncoder()); // protobuf 编码器
                            // 需要指定要对哪种对象进行解码
                            pipeline.addLast("decoder", new ProtobufDecoder(UserModel.User.getDefaultInstance()));
                            pipeline.addLast(new NettyClientHandler());
                        }
                    });
            
            ChannelFuture future = client.connect(new InetSocketAddress(host, port)).sync();
            future.channel().closeFuture().sync();
    
            System.out.println("client connect host=" + host + ", port="+port);
            
        } finally {
            group.shutdownGracefully();
        }
    }
}
