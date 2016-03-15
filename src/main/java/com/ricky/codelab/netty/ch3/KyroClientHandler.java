package com.ricky.codelab.netty.ch3;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import com.ricky.codelab.netty.model.Car;

public class KyroClientHandler extends ChannelInboundHandlerAdapter {

    private final Car message;

    /**
     * Creates a client-side handler.
     */
    public KyroClientHandler(Car message) {
    	this.message = message;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // Send the message to Server
        super.channelActive(ctx);
        
        System.out.println("client send message:"+message);
        ctx.writeAndFlush(message);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        // you can use the Object from Server here
        System.out.println("client receive msg:"+msg);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        
    	cause.printStackTrace();
        ctx.close();
    }
}