package com.mindflow.netty4.serialization.model;

import java.io.Serializable;

/**
 * @author Ricky Fung
 */
public class Response implements Serializable{

    private static final long serialVersionUID = 1937719690262222850L;

    private Long id;
    private String result;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
