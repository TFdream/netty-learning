package com.mindflow.netty4.websocket;

import com.mindflow.netty4.websocket.entity.UserInfo;
import com.mindflow.netty4.websocket.manager.ChannelsManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * @author Ricky Fung
 */
public class SocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        // 打印接收到的消息
        String body = textWebSocketFrame.text();
        UserInfo userInfo = ChannelsManager.getInstance().getUser(ctx.channel());

        LOG.info("服务端接收消息开始, 用户={}, 内容={}", userInfo.getNickname(), body);
        // 返回消息给客户端
        ctx.writeAndFlush(new TextWebSocketFrame("服务器时间: " + LocalDateTime.now() + " : " + body));

    }

    /**
     * 客户端连接的时候触发
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // LongText() 唯一的  ShortText() 不唯一
        LOG.info("handlerAdded：" + ctx.channel().id().asLongText());
        LOG.info("handlerAdded：" + ctx.channel().id().asShortText());
    }

    /**
     * 客户端断开连接的时候触发
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        LOG.info("handlerRemoved：" + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("异常发生了...", cause);
        ctx.close();
    }
}
