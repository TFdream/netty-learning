package com.bytebeats.netty4.ch2.echo;

import com.bytebeats.netty4.common.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world Server.
 * @author Ricky
 *
 */
public class EchoServer {
	private Logger logger = LoggerFactory.getLogger(getClass());

	private int port;

	public EchoServer(int port) {
		this.port = port;
	}

	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap(); // (2)
			b.group(bossGroup, workerGroup)
			.channel(NioServerSocketChannel.class) // (3)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.childHandler(new ChannelInitializer<SocketChannel>() { // (4)
				@Override
				public void initChannel(SocketChannel ch) throws Exception {

					ChannelPipeline p = ch.pipeline();
					p.addLast(new EchoServerHandler());
				}
			});

			// Bind and start to accept incoming connections.
			ChannelFuture f = b.bind(port).sync(); // (7)

			logger.info("server bind port:{}", port);

			// Wait until the server socket is closed.
			f.channel().closeFuture().sync();
		} finally {
			// Shut down all event loops to terminate all threads.
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {

		new EchoServer(Constants.PORT).run();
	}
}
