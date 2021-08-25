package com.mindflow.netty4.websocket;

import com.mindflow.netty4.common.util.StringUtils;
import com.mindflow.netty4.common.util.URLCodec;
import com.mindflow.netty4.websocket.entity.UserInfo;
import com.mindflow.netty4.websocket.manager.ChannelsManager;
import com.mindflow.netty4.websocket.manager.UserAuthManager;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ricky Fung
 */
public class HttpRequestAuthHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        String token = request.headers().get("Authorization");
        LOG.info("用户身份鉴权开始, uri={}, token={}, channelId={}", uri, token, ctx.channel().id().asLongText());

        if (StringUtils.isNotEmpty(token)) {
            //校验用户token
            UserInfo userInfo = UserAuthManager.getInstance().validateToken(token);
            LOG.info("用户身份鉴权-校验成功, uri={}, token={}", uri, token, userInfo.getNickname());

            //绑定用户登录信息到Channel
            ChannelsManager.getInstance().setUser(ctx.channel(), userInfo);

            // 传递到下一个handler：升级握手
            ctx.fireChannelRead(request.retain());
        } else {
            LOG.info("用户身份鉴权, 请先登录后再访问！");
            ctx.close();
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {  //握手完成

            Channel channel = ctx.channel();
            UserInfo userInfo = ChannelsManager.getInstance().getUser(channel);
            LOG.info("握手完成开始, 用户={}, channelId={}", userInfo.getNickname(), channel.id().asLongText());

            // 移除性能更加
            ctx.pipeline().remove(HttpRequestAuthHandler.class);

            Channel previous = ChannelsManager.getInstance().put(userInfo.getNickname(), ctx.channel());
            if(previous != null) {
                LOG.info("握手完成, 用户={}［切换设备］", userInfo.getNickname());
                ChannelFuture channelFuture = previous.writeAndFlush("您的账户在其它地方登陆");
                channelFuture.addListener(ChannelFutureListener.CLOSE);
            } else {
                WebSocketServerProtocolHandler.HandshakeComplete handshakeComplete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
                String requestUri = handshakeComplete.requestUri();
                requestUri = URLCodec.decode(requestUri);

                LOG.info("握手完成, 用户={}，URI={}", userInfo.getNickname(), requestUri);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

}
