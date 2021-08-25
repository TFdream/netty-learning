package com.mindflow.netty4.websocket.entity;

/**
 * @author Ricky Fung
 */
public class UserInfo {
    private Long id;
    /**
     * 用户昵称，要求全局唯一
     */
    private String nickname;

    private String realName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }
}
