package com.bytebeats.codelab.netty4.ch2;

import com.bytebeats.codelab.netty4.util.Constants;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * Netty4.x 对象传递
 * @author Ricky
 *
 */
public class POJOTranferServer {

	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup(Constants.IO_THREAD_NUM);
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new ObjectEncoder(),
									new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)),
									new POJOTransferServerHandler());
						}
					});

			// Bind and start to accept incoming connections.
			Channel ch = b.bind(Constants.HOST, Constants.PORT).sync().channel();

			System.out.println(String.format("Server start in host:%s port:%s", Constants.HOST, Constants.PORT));

			ch.closeFuture().sync();

		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {

		new POJOTranferServer().run();
	}
}