package com.ricky.codelab.netty.ch3;

import com.ricky.codelab.netty.ch3.serialiaztion.KyroMsgDecoder;
import com.ricky.codelab.netty.ch3.serialiaztion.KyroMsgEncoder;
import com.ricky.codelab.netty.util.Constant;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Netty4.x 自定义Decoder，Encoder进行对象传递
 * @author Ricky
 *
 */
public class KyroTransferServer {

	private final int port;

	public KyroTransferServer(int port) {
		this.port = port;
	}

	public void run() throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new KyroMsgEncoder(),
									new KyroMsgDecoder(),
									new KyroServerHandler());
						}
					});

			// Bind and start to accept incoming connections.
			b.bind(port).sync().channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {

		new KyroTransferServer(Constant.PORT).run();
	}
}
