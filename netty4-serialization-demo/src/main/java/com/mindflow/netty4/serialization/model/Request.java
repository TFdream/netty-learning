package com.mindflow.netty4.serialization.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Ricky Fung
 */
public class Request implements Serializable {

    private static final long serialVersionUID = 7300070120633180514L;

    private Long id;
    private String message;
    private List<String> tags;
    private Map<String, String> attachment;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Map<String, String> getAttachment() {
        return attachment;
    }

    public void setAttachment(Map<String, String> attachment) {
        this.attachment = attachment;
    }
}
