package com.mindflow.netty4.rpc.model;

import java.io.Serializable;

/**
 * Created by Ricky on 2017/5/9.
 */
public class Request implements Serializable {

    private static final long serialVersionUID = 7677530698715070834L;

    private String id;
    private RpcType type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RpcType getType() {
        return type;
    }

    public void setType(RpcType type) {
        this.type = type;
    }
}
