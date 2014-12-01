package com.fortify.techsupport.o4a.beans;

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

    public void setSubmit_time(String submit_time) {
        this.submit_time = submit_time;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setBody_html(String body_html) {
        this.body_html = body_html;
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

    public String getBody() {
        return body;
    }

    public String getSender() {
        return sender;
    }

    public String getBody_html() {
        return body_html;
    }
}
