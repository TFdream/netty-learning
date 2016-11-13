package com.bytebeats.netty4.sample.ch4;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServerHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private int count = 0;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        count++;
        String body = (String) msg;
        logger.info("server read msg:{}, count:{}", body, count);

//        String response = "hello from server"+System.getProperty("line.separator");
        String response = "hello from server"+"$";
        ctx.writeAndFlush(response);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        logger.error("server caught exception", cause);
        ctx.close();
    }

}