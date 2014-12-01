package com.fortify.techsupport.o4a.beans;

import java.util.List;

/**
 * Created by Dmitrii Soin on 27/11/14.
 */
public class StatsBean {
    private List<StatItemBean> topDiscussedEver;
    private List<StatItemBean> topPostersEver;
    private List<StatItemBean> recent;


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
