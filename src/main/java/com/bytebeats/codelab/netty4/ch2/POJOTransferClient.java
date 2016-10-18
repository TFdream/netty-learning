package com.bytebeats.codelab.netty4.ch2;

import com.bytebeats.codelab.netty4.model.User;
import com.bytebeats.codelab.netty4.util.Constants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.ArrayList;
import java.util.List;

public class POJOTransferClient {

	private List<User> message;

	public POJOTransferClient(List<User> message) {
		this.message = message;
	}

	public void send() throws InterruptedException {
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup(10);

		try {
			bootstrap.group(eventLoopGroup)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new ObjectEncoder(),
									new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)),
									new POJOTransferClientHandler(message));
						}
					});

			ChannelFuture future = bootstrap.connect(Constants.HOST, Constants.PORT).sync();

			future.channel().closeFuture().sync();
		} finally {
			eventLoopGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {
		
		final List<User> message = new ArrayList<>();
		for(int i=0;i<5;i++){
			
			User user = new User();
			user.setId(i+1);
			user.setName("Ricky P"+i);
			user.setAge(20+i);
			
			message.add(user);
		}

		new POJOTransferClient(message).send();
	}
}