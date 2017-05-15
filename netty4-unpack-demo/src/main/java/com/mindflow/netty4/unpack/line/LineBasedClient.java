package com.mindflow.netty4.unpack.line;

import com.mindflow.netty4.common.Constants;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class LineBasedClient {

	private String host;
	private int port;

	public LineBasedClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void send() throws InterruptedException {

		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
			.channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {

					ChannelPipeline p = ch.pipeline();
					p.addLast(new LineBasedFrameDecoder(1024));
					p.addLast(new StringDecoder());
					p.addLast(new StringEncoder());

					p.addLast(new LineBasedClientHandler());
				}
			});

			ChannelFuture future = b.connect(Constants.HOST, Constants.PORT).sync();

			future.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception {

		new LineBasedClient(Constants.HOST, Constants.PORT).send();
	}
}