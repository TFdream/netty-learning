package com.mindflow.netty4.rpc.core;

import com.mindflow.netty4.rpc.model.PingMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * refer: https://netty.io/4.1/api/io/netty/handler/timeout/IdleStateHandler.html
 *
 * @author Ricky Fung
 */
public class NettyConnHandler extends ChannelDuplexHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent e = (IdleStateEvent) evt;
            if (e.state() == IdleState.READER_IDLE) {
                logger.info("READER_IDLE 事件触发, 关闭连接");/*读超时*/
                ctx.close();
            } else if (e.state() == IdleState.WRITER_IDLE) {
                logger.info("WRITER_IDLE 事件触发");
                ctx.writeAndFlush(new PingMessage());
            } else if (e.state() == IdleState.ALL_IDLE) {
                logger.info("ALL_IDLE 事件触发, 关闭连接");
                ctx.close();
            }
        }
    }
}
