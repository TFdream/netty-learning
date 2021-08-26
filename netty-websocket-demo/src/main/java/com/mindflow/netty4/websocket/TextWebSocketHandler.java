package com.mindflow.netty4.websocket;

import com.mindflow.netty4.common.util.NettyUtils;
import com.mindflow.netty4.websocket.entity.UserInfo;
import com.mindflow.netty4.websocket.manager.UserChannelManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * @author Ricky Fung
 */
public class TextWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        // 打印接收到的消息
        String body = textWebSocketFrame.text();
        UserInfo userInfo = UserChannelManager.getInstance().getUser(ctx.channel());

        LOG.info("服务端接收消息开始, channelId={}, 用户={}, 内容={}", NettyUtils.getChannelId(ctx), userInfo.getNickname(), body);

        // 返回消息给客户端
        ctx.writeAndFlush(new TextWebSocketFrame("服务器时间: " + LocalDateTime.now() + " : " + body));
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            LOG.info("客户端握手完成, channelId={}", NettyUtils.getChannelId(ctx));
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        LOG.info("客户端建立连接, channelId={}", NettyUtils.getChannelId(ctx));
        //ChannelsManager.getInstance().put(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        LOG.info("客户端断开连接, channelId={}", NettyUtils.getChannelId(ctx));
        UserChannelManager.getInstance().remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.error("客户端连接异常, channelId={}", NettyUtils.getChannelId(ctx), cause);
        UserChannelManager.getInstance().remove(ctx.channel());
        ctx.close();
    }

}
