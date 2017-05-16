package com.mindflow.netty4.serialization.model;

/**
 * @author Ricky Fung
 */
public class Response {
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
