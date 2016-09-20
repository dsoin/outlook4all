package com.dsoin.o4a.beans;

import java.util.List;

/**
 * Created by Dmitrii Soin on 28/11/14.
 */
public class SearchResultsBean {
    private long totalHits;
    private long took;
    private List<SearchBean> searchResults;

    public long getTotalHits() {
        return totalHits;
    }

    public void setTotalHits(long totalHits) {
        this.totalHits = totalHits;
    }

    public long getTook() {
        return took;
    }

    public void setTook(long took) {
        this.took = took;
    }

    public List<SearchBean> getSearchResults() {
        return searchResults;
    }

    public void setSearchResults(List<SearchBean> searchResults) {
        this.searchResults = searchResults;
    }
}
