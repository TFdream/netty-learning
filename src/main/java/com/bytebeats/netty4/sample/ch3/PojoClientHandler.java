package com.bytebeats.netty4.sample.ch3;

import com.bytebeats.netty4.sample.domain.ch3.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PojoClientHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // Send the message to Server
        Message msg = new Message();
        msg.setId(10);
        msg.setFrom("client");
        msg.setBody("hello from client");

        logger.info("client send message id:{}", msg.getId());
        ctx.writeAndFlush(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        Message m = (Message) msg;
        logger.info("client read msg id:{}, body:{}", m.getId(),m.getBody());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        logger.error("client caught exception", cause);
        ctx.close();
    }
}