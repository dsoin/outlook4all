package com.fortify.techsupport.o4a.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dmitrii Soin on 27/11/14.
 */
public class StatsBean {
    private Map<String,Long> topDiscussedEver;
    private Map<String,Long> topPostersEver;
    private Map<String,Long> recent;

    public void setRecent(Map<String, Long> recent) {
        this.recent = recent;
    }

    public void setTopPostersEver(Map<String, Long> topPostersEver) {
        this.topPostersEver = topPostersEver;
    }

    public void setTopDiscussedEver(Map<String, Long> topDiscussedEver) {
        this.topDiscussedEver = topDiscussedEver;
    }
}
