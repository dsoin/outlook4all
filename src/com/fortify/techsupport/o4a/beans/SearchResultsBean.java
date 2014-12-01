package com.fortify.techsupport.o4a.beans;

import java.util.List;

/**
 * Created by Dmitrii Soin on 28/11/14.
 */
public class SearchResultsBean {
    private long totalHits;
    private long took;
    private List<SearchBean> searchResults;

    public void setTook(long took) {
        this.took = took;
    }

    public void setSearchResults(List<SearchBean> searchResults) {
        this.searchResults = searchResults;
    }

    public void setTotalHits(long totalHits) {
        this.totalHits = totalHits;
    }

    public long getTotalHits() {
        return totalHits;
    }

    public long getTook() {
        return took;
    }

    public List<SearchBean> getSearchResults() {
        return searchResults;
    }
}
