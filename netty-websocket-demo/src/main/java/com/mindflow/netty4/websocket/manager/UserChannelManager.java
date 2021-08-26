package com.mindflow.netty4.websocket.manager;

import com.mindflow.netty4.websocket.entity.UserInfo;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Ricky Fung
 */
public class UserChannelManager {
    private static final AttributeKey<UserInfo> USER_KEY = AttributeKey.valueOf("_uid");

    private final ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    public static UserChannelManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public Channel put(Channel channel) {
        UserInfo userInfo = getUser(channel);
        if (userInfo == null) {
            return null;
        }
        return channelMap.put(userInfo.getNickname(), channel);
    }

    public Channel put(String key, Channel channel) {
        return channelMap.put(key, channel);
    }

    public Channel remove(String id) {
        return channelMap.remove(id);
    }

    public Channel remove(Channel channel) {
        UserInfo userInfo = getUser(channel);
        if (userInfo == null) {
            return null;
        }
        return channelMap.remove(userInfo.getNickname());
    }

    //=========
    public void setUser(Channel channel, UserInfo userInfo) {
        channel.attr(USER_KEY).set(userInfo);
    }

    public UserInfo getUser(Channel channel) {
        return channel.attr(USER_KEY).get();
    }

    //=========
    private static class SingletonHolder {
        private static final UserChannelManager INSTANCE = new UserChannelManager();

    }
}
