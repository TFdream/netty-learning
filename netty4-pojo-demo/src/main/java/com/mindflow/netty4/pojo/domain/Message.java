package com.mindflow.netty4.pojo.domain;

import java.io.Serializable;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 * @create 2016-11-13 14:46
 */
public class Message implements Serializable {
    private static final long serialVersionUID = -7090624835490794060L;

    private long id;
    private String from;
    private String body;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
