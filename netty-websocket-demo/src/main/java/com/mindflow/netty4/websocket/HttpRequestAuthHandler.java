package com.mindflow.netty4.websocket;

import com.mindflow.netty4.common.util.NettyUtils;
import com.mindflow.netty4.common.util.StringUtils;
import com.mindflow.netty4.websocket.entity.UserInfo;
import com.mindflow.netty4.websocket.manager.UserAuthManager;
import com.mindflow.netty4.websocket.manager.UserChannelManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ricky Fung
 */
public class HttpRequestAuthHandler extends ChannelInboundHandlerAdapter {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;

            //http request uri: /chat?accessKey=hello
            String requestUri = request.uri();
            String token = request.headers().get("Authorization");
            if (StringUtils.isEmpty(token)) {
                token = NettyUtils.getUriParam(requestUri, "token");
            }
            LOG.info("用户身份鉴权开始, uri={}, token={}, channelId={}, clientIp={}",
                    requestUri, token, NettyUtils.getChannelId(ctx), NettyUtils.getClientIp(ctx));

            if (StringUtils.isNotEmpty(token)) {
                //校验用户token
                UserInfo userInfo = UserAuthManager.getInstance().validateToken(token);
                LOG.info("用户身份鉴权-校验token成功, uri={}, token={}", requestUri, token, userInfo.getNickname());

                //绑定用户登录信息到Channel
                UserChannelManager.getInstance().setUser(ctx.channel(), userInfo);

                //缓存起来
                UserChannelManager.getInstance().put(userInfo.getNickname(), ctx.channel());

                //移除鉴权
                //ctx.pipeline().remove(this);

                // 传递到下一个handler：升级握手
                ctx.fireChannelRead(request.retain());

                LOG.info("用户身份鉴权-校验通过, uri={}, token={}, nickname={}, channelId={}",
                        requestUri, token, userInfo.getNickname(), NettyUtils.getChannelId(ctx));
            } else {
                LOG.info("用户身份鉴权, 请先登录后再访问！");
                ctx.channel().close();
            }
        } else {
            super.channelRead(ctx, msg);
        }
    }

}
