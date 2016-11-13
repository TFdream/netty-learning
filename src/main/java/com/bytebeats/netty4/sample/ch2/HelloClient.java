package com.bytebeats.netty4.sample.ch2;

import com.bytebeats.netty4.sample.util.Constants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class HelloClient {

	private String host;
	private int port;

	public HelloClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void send() throws InterruptedException {

		EventLoopGroup group = new NioEventLoopGroup(10);

		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {

					ChannelPipeline p = ch.pipeline();
					p.addLast(new StringEncoder());
					p.addLast(new StringDecoder());
					p.addLast(new HelloClientHandler());
				}
			});

			ChannelFuture future = b.connect(Constants.HOST, Constants.PORT).sync();

			future.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {

		new HelloClient(Constants.HOST, Constants.PORT).send();
	}
}