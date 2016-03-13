package com.ricky.codelab.netty.ch1;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Handles a client-side channel.
 */
public class HelloWorldClientHandler extends ChannelInboundHandlerAdapter { // (1)

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// Send the message to Server
		super.channelActive(ctx);
		System.out.println("client send message");
		ctx.writeAndFlush("Hello world![from client]");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
		
		System.out.println("client receive:" + msg);
		ctx.close();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}