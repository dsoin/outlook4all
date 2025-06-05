package com.dsoin.o4a;

import com.dsoin.o4a.beans.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Created by Dmitrii Soin on 27/11/14.
 */
public class ESHelper {
    private final static Logger log = LogManager.getLogger(ESHelper.class);
    private Client client;


    public ESHelper(Client client) {
        this.client = client;
    }

    public SearchResultsBean searchEmails(String query, int from, boolean phrase) {

        SearchResultsBean sb = new SearchResultsBean();
        SearchResponse response = client.prepareSearch("data").setSearchType(SearchType.QUERY_THEN_FETCH).
                setQuery(QueryBuilders.multiMatchQuery(query, "body", "topic").type(phrase ? MultiMatchQueryBuilder.Type.PHRASE_PREFIX : MultiMatchQueryBuilder.Type.BEST_FIELDS)).
                highlighter(new HighlightBuilder().field("body",500,1).field("topic",200,1)).
                addStoredField("topic").
                addStoredField("body").
                addStoredField("sender").
                addStoredField("submit_time").
                addStoredField("has_attachment").
                setFrom(from).
                execute().actionGet();
        sb.setTotalHits(response.getHits().getTotalHits().value);
        sb.setTook(response.getTook().getMillis());
        List<SearchBean> sbs = new ArrayList<>();
        for (SearchHit hit : response.getHits().getHits()) {
            SearchBean bean = new SearchBean();
            bean.setSubmit_time((String) hit.getFields().get("submit_time").getValue());
            bean.setTopic((String) hit.getFields().get("topic").getValue());
            if (hit.getFields().get("has_attachment") != null)
                bean.setHasAttachment(true);
            if (hit.getHighlightFields().get("body") != null)
                bean.setBody(hit.getHighlightFields().get("body").getFragments()[0].string());
            sbs.add(bean);

        }
        sb.setSearchResults(sbs);
        return sb;

    }

    public List<SearchBean> getConversation(String topic) {
        List<SearchBean> convs = new ArrayList<>();

        SearchResponse response = client.prepareSearch("data").setSearchType(SearchType.QUERY_THEN_FETCH).
                setQuery(QueryBuilders.matchPhrasePrefixQuery("topic.keyword", topic)).
                addSort(SortBuilders.fieldSort("submit_time").order(SortOrder.DESC)).setSize(1000).
                execute().actionGet();
        for (SearchHit hit : response.getHits().getHits()) {
            SearchBean sb = new SearchBean();
            var src = hit.getSourceAsMap();
            sb.setBody((String) src.get("body"));
            sb.setBody_html((String) src.get("body_html"));
            sb.setSender((String) src.get("sender"));
            sb.setSubmit_time((String) src.get("submit_time"));
            sb.setTopic((String) src.get("topic"));
            if (src.get("has_attachment") != null) {
                sb.setAttachments(getAttachments(hit.getId()));
                if (sb.getAttachments().size() > 0)
                    sb.setHasAttachment(true);
            }
            convs.add(sb);
        }


        return convs;

    }

    private List<AttachmentBean> getAttachments(String emailID) {
        List<AttachmentBean> res = new ArrayList<>();
        SearchResponse response = client.prepareSearch("attachments").
                setQuery(QueryBuilders.termQuery("email_id", emailID)).
                addStoredField("filename").
                addStoredField("size").
                execute().actionGet();
        log.error(response);
        for (SearchHit hit : response.getHits().getHits()) {
            AttachmentBean ab = new AttachmentBean();
            ab.setFilename((String) hit.getFields().get("filename").getValue());
            ab.setId(hit.getId());
            ab.setSize((Integer) hit.getFields().get("size").getValue());
            if (!(
                    (ab.getFilename().endsWith("png") || ab.getFilename().endsWith("jpg") || ab.getFilename().endsWith("gif")) && ab.getSize() < 10000))
                res.add(ab);
        }

        return res;
    }

    public AttachmentBean getAttachment(String id) throws IOException {
        AttachmentBean ab = new AttachmentBean();
        SearchResponse response = client.prepareSearch("attachments").
                setQuery(QueryBuilders.termQuery("_id", id)).
                addStoredField("attachment").
                addStoredField("mime").
                addStoredField("filename").
                execute().actionGet();
        for (SearchHit hit : response.getHits().getHits()) {
            BytesArray bytes = hit.getFields().get("attachment").getValue();
            ab.setData(Base64.getDecoder().decode(bytes.array()));
            log.error(bytes.array());
            ab.setMime(hit.getFields().get("mime").getValue());
            ab.setFilename(hit.getFields().get("filename").getValue());
        }

        return ab;
    }

    public StatsBean getStats() {
        StatsBean sb = new StatsBean();
//get top discussed
        SearchResponse response = client.prepareSearch("data").setSize(0).
                addAggregation(AggregationBuilders.terms("stats").field("topic.keyword").size(20)).
                execute().actionGet();
        log.error(response);
        sb.setTopDiscussedEver(pullAggsResult(response.getAggregations().get("stats")));
//get top posters
        response = client.prepareSearch("data").
                addAggregation(AggregationBuilders.terms("stats").field("sender.keyword").size(20)).
                execute().actionGet();
        sb.setTopPostersEver(pullAggsResult(response.getAggregations().get("stats")));

// get recent
        response = client.prepareSearch("data").
                addAggregation(AggregationBuilders.
                        filter("filter1m",QueryBuilders.rangeQuery("submit_time").to("now").from("now-1y")).
                        subAggregation(AggregationBuilders.terms("stats").field("topic.keyword").size(40).order(BucketOrder.aggregation("ta", false)).
                                subAggregation(AggregationBuilders.avg("ta").field("submit_time"))))
                .execute().actionGet();
        sb.setRecent(pullAggsResult(((Filter) response.
                getAggregations().get("filter1m")).getAggregations().get("stats")));
//get index stats
        IndicesStatsResponse is = client.admin().indices().prepareStats("data", "attachments").execute().actionGet();
        sb.setEmailsCount(is.getIndex("data").getTotal().docs.getCount());
        sb.setEmailsSize(is.getIndex("data").getTotal().getStore().sizeInBytes());
        sb.setAttachmentsCount(is.getIndex("attachments").getTotal().docs.getCount());
        sb.setAttachmentsSize(is.getIndex("attachments").getTotal().getStore().sizeInBytes());

        response = client.prepareSearch("data").setSearchType(SearchType.QUERY_THEN_FETCH).setSize(10).
                setQuery(QueryBuilders.matchAllQuery()).addSort(SortBuilders.fieldSort("submit_time").order(SortOrder.DESC)).
                addStoredField("submit_time").
                execute().actionGet();
        sb.setLastPost((String) response.getHits().getHits()[0].getFields().get("submit_time").getValue());
        response = client.prepareSearch("data").setSearchType(SearchType.QUERY_THEN_FETCH).
                setQuery(QueryBuilders.matchAllQuery()).addSort(SortBuilders.fieldSort("submit_time").order(SortOrder.ASC)).
                addStoredField("submit_time").
                execute().actionGet();
        sb.setFirstPost((String) response.getHits().getHits()[0].getFields().get("submit_time").getValue());
        return sb;
    }

    private List<StatItemBean> pullAggsResult(Aggregation agg) {
        List<StatItemBean> si = new ArrayList<>();
        if (agg != null) {
            for (Terms.Bucket statBusket : ((Terms) agg).getBuckets()) {
                StatItemBean sib = new StatItemBean();
                sib.setText((String) statBusket.getKey());
                sib.setValue(statBusket.getDocCount());
                si.add(sib);
            }
        }
        return si;
    }

}
