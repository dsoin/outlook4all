package com.fortify.techsupport.o4a;

import com.fortify.techsupport.o4a.beans.SearchBean;
import com.fortify.techsupport.o4a.beans.SearchResultsBean;
import com.fortify.techsupport.o4a.beans.StatItemBean;
import com.fortify.techsupport.o4a.beans.StatsBean;
import org.apache.commons.logging.impl.SimpleLog;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dmitrii Soin on 27/11/14.
 */
public class ESHelper {
    final static Client client = new TransportClient()
            .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
    final SimpleLog log = new SimpleLog(this.getClass().getName());

    public SearchResultsBean searchEmails(String query, int from) {
        SearchResultsBean sb = new SearchResultsBean();
        SearchResponse response = client.prepareSearch("emails").setSearchType(SearchType.QUERY_THEN_FETCH).
                setQuery(QueryBuilders.multiMatchQuery(query, "body", "topic")).
                addHighlightedField("body", 500, 1).addHighlightedField("topic", 50, 1).
                addFields("topic", "sender", "submit_time").
                setFrom(from).
                execute().actionGet();

        sb.setTotalHits(response.getHits().getTotalHits());
        sb.setTook(response.getTook().getMillis());
        List<SearchBean> sbs = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            SearchBean bean = new SearchBean();
            bean.setSubmit_time((String) hit.getFields().get("submit_time").getValue());
            bean.setTopic((String) hit.getFields().get("topic").getValue());
            if (hit.getHighlightFields().get("body") != null)
                bean.setBody(hit.getHighlightFields().get("body").getFragments()[0].string());
            sbs.add(bean);

        }
        sb.setSearchResults(sbs);
        return sb;

    }

    public List<SearchBean> getConversation(String topic) {
        List<SearchBean> convs = new ArrayList<>();

        SearchResponse response = client.prepareSearch("emails").setSearchType(SearchType.QUERY_THEN_FETCH).
                setQuery(QueryBuilders.matchPhrasePrefixQuery("topic.raw", topic)).
                addSort(SortBuilders.fieldSort("submit_time").order(SortOrder.DESC)).setSize(Integer.MAX_VALUE).
                execute().actionGet();
        for (SearchHit hit : response.getHits().getHits()) {
            SearchBean sb = new SearchBean();
            sb.setBody((String) hit.getSource().get("body"));
            sb.setBody_html((String) hit.getSource().get("body_html"));
            sb.setSender((String) hit.getSource().get("sender"));
            sb.setSubmit_time((String) hit.getSource().get("submit_time"));
            sb.setTopic((String) hit.getSource().get("topic"));
            convs.add(sb);
        }

        //log.info(response);


        return convs;

    }

    public StatsBean getStats() {
        StatsBean sb = new StatsBean();
//get top discussed
        SearchResponse response = client.prepareSearch("emails").setSearchType(SearchType.COUNT).
                addAggregation(AggregationBuilders.terms("stats").field("topic.raw").size(20)).
                execute().actionGet();
        sb.setTopDiscussedEver(pullAggsResult(response.getAggregations().get("stats")));
//get top posters
        response = client.prepareSearch("emails").setSearchType(SearchType.COUNT).
                addAggregation(AggregationBuilders.terms("stats").field("sender.raw").size(20)).
                execute().actionGet();
        sb.setTopPostersEver(pullAggsResult(response.getAggregations().get("stats")));

// get recent
        response = client.prepareSearch("emails").setSearchType(SearchType.COUNT).
                addAggregation(AggregationBuilders.filter("filter1m").filter(FilterBuilders.rangeFilter("submit_time").to("now").from("now-1M")).
                        subAggregation(AggregationBuilders.terms("stats").field("topic.raw").size(40).order(Terms.Order.aggregation("ta", false)).
                                subAggregation(AggregationBuilders.avg("ta").field("submit_time"))))
                .execute().actionGet();
        sb.setRecent(pullAggsResult(((Filter) response.getAggregations().get("filter1m")).getAggregations().get("stats")));

        return sb;
    }

    private List<StatItemBean> pullAggsResult(Aggregation agg) {
        List<StatItemBean> si = new ArrayList<>();
        if (agg != null) {
            for (Terms.Bucket statBusket : ((Terms) agg).getBuckets()) {
                StatItemBean sib = new StatItemBean();
                sib.setText(statBusket.getKey());
                sib.setValue(statBusket.getDocCount());
                si.add(sib);
            }
        }
        return si;
    }

}
