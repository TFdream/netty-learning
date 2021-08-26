package com.mindflow.netty4.websocket;

import com.mindflow.netty4.common.util.JsonUtils;
import com.mindflow.netty4.websocket.entity.UserInfo;
import com.mindflow.netty4.websocket.manager.UserAuthManager;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Ricky Fung
 */
public class AppTest {

    @Test
    public void testApp() {

    }

    @Test
    public void testGenToken() {
        String token = UserAuthManager.getInstance().genToken(15L, "Ricky");
        System.out.println(token);
    }

    @Test
    @Ignore
    public void testDecodeToken() {
        UserInfo userInfo = UserAuthManager.getInstance().validateToken("eyJpZCI6MTUsIm5pY2tuYW1lIjoiUmlja3kifQ==");
        System.out.println(JsonUtils.toJson(userInfo));
    }
}
