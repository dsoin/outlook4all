package com.fortify.techsupport.o4a.beans;

import java.util.List;

/**
 * Created by Dmitrii Soin on 27/11/14.
 */
public class StatsBean {
    private List<StatItemBean> topDiscussedEver;
    private List<StatItemBean> topPostersEver;
    private List<StatItemBean> recent;
    private long emailsCount;
    private long emailsSize;
    private long attachmentsCount;
    private long attachmentsSize;
    private String firstPost;
    private String lastPost;

    public String getFirstPost() {
        return firstPost;
    }

    public void setFirstPost(String firstPost) {
        this.firstPost = firstPost;
    }

    public String getLastPost() {
        return lastPost;
    }

    public void setLastPost(String lastPost) {
        this.lastPost = lastPost;
    }

    public long getEmailsCount() {
        return emailsCount;
    }

    public void setEmailsCount(long emailsCount) {
        this.emailsCount = emailsCount;
    }

    public long getEmailsSize() {
        return emailsSize;
    }

    public void setEmailsSize(long emailsSize) {
        this.emailsSize = emailsSize;
    }

    public long getAttachmentsCount() {
        return attachmentsCount;
    }

    public void setAttachmentsCount(long attachmentsCount) {
        this.attachmentsCount = attachmentsCount;
    }

    public long getAttachmentsSize() {
        return attachmentsSize;
    }

    public void setAttachmentsSize(long attachmentsSize) {
        this.attachmentsSize = attachmentsSize;
    }

    public void setRecent(List<StatItemBean> recent) {
        this.recent = recent;
    }

    public void setTopPostersEver(List<StatItemBean> topPostersEver) {
        this.topPostersEver = topPostersEver;
    }

    public void setTopDiscussedEver(List<StatItemBean> topDiscussedEver) {
        this.topDiscussedEver = topDiscussedEver;
    }

    public List<StatItemBean> getTopDiscussedEver() {
        return topDiscussedEver;
    }

    public List<StatItemBean> getTopPostersEver() {
        return topPostersEver;
    }

    public List<StatItemBean> getRecent() {
        return recent;
    }


}
