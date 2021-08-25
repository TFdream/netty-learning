package com.mindflow.netty4.websocket.manager;

import com.mindflow.netty4.common.util.Base64Codec;
import com.mindflow.netty4.common.util.JsonUtils;
import com.mindflow.netty4.websocket.entity.UserInfo;

/**
 * @author Ricky Fung
 */
public class UserAuthManager {

    public static UserAuthManager getInstance() {
        return UserAuthManager.SingletonHolder.INSTANCE;
    }

    public UserInfo validateToken(String token) {
        String json = Base64Codec.decode(token);
        return JsonUtils.parseObject(json, UserInfo.class);
    }

    public String genToken(Long id, String nickname) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(id);
        userInfo.setNickname(nickname);
        return Base64Codec.encode(JsonUtils.toJson(userInfo));
    }

    private static class SingletonHolder {
        private static final UserAuthManager INSTANCE = new UserAuthManager();

    }
}
