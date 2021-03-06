package com.dsoin.o4a.beans;

import java.util.List;

/**
 * Created by Dmitrii Soin on 27/11/14.
 */
public class SearchBean {
    private String topic;
    private String highlite;
    private String submit_time;
    private String body;
    private String sender;
    private String body_html;
    private boolean hasAttachment = false;
    private List<AttachmentBean> attachments;

    public List<AttachmentBean> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentBean> attachments) {
        this.attachments = attachments;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getHighlite() {
        return highlite;
    }

    public void setHighlite(String highlite) {
        this.highlite = highlite;
    }

    public String getSubmit_time() {
        return submit_time;
    }

    public void setSubmit_time(String submit_time) {
        this.submit_time = submit_time;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getBody_html() {
        return body_html;
    }

    public void setBody_html(String body_html) {
        this.body_html = body_html;
    }
}
