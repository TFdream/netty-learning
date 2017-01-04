package com.bytebeats.netty4.ch2;

import com.bytebeats.netty4.ch2.domain.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PojoServerHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {

        Message body = (Message) msg;
        logger.info("server read msg id:{}, body:{}", body.getId(), body.getBody());

        Message response = new Message();
        response.setId(1024);
        response.setFrom("server");
        response.setBody("hello from server");
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        logger.error("server caught exception", cause);
        ctx.close();
    }

}