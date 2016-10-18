package com.bytebeats.codelab.netty4.ch3;

import com.bytebeats.codelab.netty4.ch3.serialiaztion.KyroMsgDecoder;
import com.bytebeats.codelab.netty4.ch3.serialiaztion.KyroMsgEncoder;
import com.bytebeats.codelab.netty4.model.Car;
import com.bytebeats.codelab.netty4.util.Constants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class KyroTransferClient {
	private String host;
	private int port;
	private Car message;

	public KyroTransferClient(String host, int port, Car message) {
		this.host = host;
		this.port = port;
		this.message = message;
	}

	public void send() throws InterruptedException {
		
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

		try {
			bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new KyroMsgEncoder(),
									new KyroMsgDecoder(),
									new KyroClientHandler(message));
						}
					});

			ChannelFuture future = bootstrap.connect(host, port).sync();

			future.channel().closeFuture().sync();
		} finally {
			eventLoopGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		
		Car message = new Car();
		message.setName("X5");
		message.setBrand("BMW");
		message.setPrice(52.6);
		message.setSpeed(200);

		new KyroTransferClient(Constants.HOST, Constants.PORT, message).send();
	}
}