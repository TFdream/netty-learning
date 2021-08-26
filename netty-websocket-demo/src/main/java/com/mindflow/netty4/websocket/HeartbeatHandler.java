package com.mindflow.netty4.websocket;

import com.mindflow.netty4.common.util.NettyUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Ricky Fung
 */
public class HeartbeatHandler extends IdleStateHandler {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    public HeartbeatHandler(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
        super(readerIdleTime, writerIdleTime, allIdleTime, unit);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent event) throws Exception {
        if (event == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT) {
            LOG.info("检测到客户端空闲，关闭连接, clientIp={}", NettyUtils.getClientIp(ctx));
            ctx.close();
            return;
        }
        super.channelIdle(ctx, event);
    }
}
