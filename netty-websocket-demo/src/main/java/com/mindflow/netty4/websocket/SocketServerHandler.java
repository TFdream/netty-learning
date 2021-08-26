package com.mindflow.netty4.websocket;

import com.mindflow.netty4.websocket.entity.UserInfo;
import com.mindflow.netty4.websocket.manager.UserChannelManager;
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
        UserInfo userInfo = UserChannelManager.getInstance().getUser(ctx.channel());

        LOG.info("服务端接收消息开始, 用户={}, 内容={}", userInfo.getNickname(), body);
        // 返回消息给客户端
        ctx.writeAndFlush(new TextWebSocketFrame("服务器时间: " + LocalDateTime.now() + " : " + body));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        LOG.info("客户端建立连接：" + ctx.channel().id().asLongText());
        //ChannelsManager.getInstance().put(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LOG.info("客户端断开连接：" + ctx.channel().id().asLongText());
        UserChannelManager.getInstance().remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("客户端连接异常", cause);
        UserChannelManager.getInstance().remove(ctx.channel());
        ctx.close();
    }
}
