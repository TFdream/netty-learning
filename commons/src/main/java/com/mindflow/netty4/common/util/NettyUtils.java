package com.mindflow.netty4.common.util;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ricky Fung
 */
public abstract class NettyUtils {

    public static String getClientIp(ChannelHandlerContext ctx) {
        return getClientIp(ctx.channel());
    }

    public static String getClientIp(Channel channel) {
        InetSocketAddress ipSocket = (InetSocketAddress) channel.remoteAddress();
        String clientIp = ipSocket.getAddress().getHostAddress();
        return clientIp;
    }

    public static String getChannelId(ChannelHandlerContext ctx) {
        return ctx.channel().id().asShortText();
    }

    public static String getChannelId(Channel channel) {
        return channel.id().asShortText();
    }

    //==========
    public static String getUriParam(String requestUri, String paramName) {
        Map<String, String> map = parseUriParams(requestUri);
        return map != null ? map.get(paramName) : null;
    }

    /**
     * 解析uri上的请求参数
     * @param requestUri
     * @return
     */
    public static Map<String, String> parseUriParams(String requestUri) {
        if (StringUtils.isEmpty(requestUri)) {
            return null;
        }
        int idx = requestUri.indexOf("?");
        if (idx < 0) {
            return null;
        }
        String queryString = requestUri.substring(idx+1);
        if(StringUtils.isNotEmpty(queryString)) {
            String[] paramsArr = queryString.split("&");
            HashMap<String, String> params = new HashMap<>();
            for(String param : paramsArr) {
                String [] keyValue = param.split("=");
                if(keyValue != null && keyValue.length >= 2) {
                    params.put(keyValue[0], keyValue[1]);
                }
            }
            return params;
        }
        return null;
    }
}
