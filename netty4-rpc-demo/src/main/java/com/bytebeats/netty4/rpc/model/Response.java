package com.bytebeats.netty4.rpc.model;

import java.io.Serializable;

/**
 * Created by Ricky on 2017/5/9.
 */
public class Response implements Serializable {

    private static final long serialVersionUID = 7350389840006336413L;

    private String id;
    private Object result;
    private Throwable ex;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getEx() {
        return ex;
    }

    public void setEx(Throwable ex) {
        this.ex = ex;
    }
}
