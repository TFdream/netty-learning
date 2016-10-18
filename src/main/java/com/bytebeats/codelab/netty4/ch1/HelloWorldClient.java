package com.bytebeats.codelab.netty4.ch1;

import com.bytebeats.codelab.netty4.util.Constants;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Hello world Client.
 * @author Ricky
 *
 */
public class HelloWorldClient {
	private String host;
	private int port;

	public HelloWorldClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void send() throws InterruptedException {
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

		try {
			bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new StringEncoder(), new StringDecoder(), new HelloWorldClientHandler());
						}
					});

			ChannelFuture future = bootstrap.connect(host, port).sync();

			future.channel().closeFuture().sync();
		} finally {
			eventLoopGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {

		new HelloWorldClient(Constants.HOST, Constants.PORT).send();
	}
}
