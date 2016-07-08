package com.wanjia.utils;

import com.wanjia.vo.ESAggResultVo;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.collect.HppcMaps;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.sum.Sum;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by blake on 2016/6/22.
 */
public class ElasticSearchClient {

    private Client client;

    public ElasticSearchClient(String clustername, String hosts, int port) {

        Settings settings = Settings.settingsBuilder().put("cluster.name", clustername).put("client.transport.sniff", true).build();
        TransportClient transportClient = TransportClient.builder().settings(settings).build();

        String serverhosts[] = hosts.split(",");
        for (String host : serverhosts) {
            transportClient.addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(host, port)));
        }
        client = transportClient;
    }


    public List<Object> queryDataFromEsWithoutPaging(QueryBuilder queryBuilder, QueryBuilder postFilter,  String index, String type, Class clazz) throws Exception{

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type).setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(queryBuilder).setFrom(0).setSize(10000);//max_result_window

        if(postFilter != null ){
            searchRequestBuilder.setPostFilter(postFilter) ;
        }

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        SearchHits searchHits = searchResponse.getHits();
        long totalHits = searchHits.totalHits();
        List objectList = new ArrayList() ;
        if (totalHits > 0) {
            convertJsonToObject(searchHits,clazz,objectList);
        }

        return objectList ;

    }



    public void queryDataFromEsWithPostFilter(QueryBuilder queryBuilder, QueryBuilder postFilter , List<SortField> sortFields, GeoDistanceSortBuilder geoDistanceSortBuilder , String index, String type, int from, int size, Class clazz, PageResult pageResult) throws Exception{

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type).setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(queryBuilder).setFrom(from).setSize(size);

        if(postFilter != null){
            searchRequestBuilder.setPostFilter(postFilter) ;
        }

        if(geoDistanceSortBuilder != null){
            searchRequestBuilder.addSort(geoDistanceSortBuilder) ;
        }

        if(sortFields != null){
            for(SortField sortField : sortFields){
                searchRequestBuilder.addSort(sortField.getField(),sortField.getSortOrder()) ;
            }
        }

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        SearchHits searchHits = searchResponse.getHits();
        long totalHits = searchHits.totalHits();
        if(pageResult.getTotalNumber() == 0){
            pageResult.setTotalNumber(totalHits);
        }

        List objectList = new ArrayList() ;
        if (totalHits > 0) {
            convertJsonToObject(searchHits,clazz,objectList);
        }
        pageResult.setResult(objectList);
    }


    public void queryDataFromEsWithPostFilterAndAggregation(QueryBuilder queryBuilder, QueryBuilder postFilter, AggregationBuilder aggregationBuilder,String groupName,String subAggName,
                                                            String aggType , String index, String type, int from, int size, PageResult pageResult) throws Exception{

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type).setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(queryBuilder).setFrom(from).setSize(size);

        if(postFilter != null){
            searchRequestBuilder.setPostFilter(postFilter) ;
        }

        if(aggregationBuilder != null ){
            searchRequestBuilder.addAggregation(aggregationBuilder) ;
        }

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        List objectList = new ArrayList() ;

        Terms agg =  searchResponse.getAggregations().get(groupName) ;
        for (Terms.Bucket entry : agg.getBuckets()) {
            ESAggResultVo vo = new ESAggResultVo() ;
            String key = entry.getKey().toString();// bucket key
            vo.setKey(key);
            long docCount = entry.getDocCount();
            vo.setCount(docCount);  // Doc count
            ESAggResultVo subVo = new ESAggResultVo() ;
            if(aggType.equals("sum")){
                Sum sum  = entry.getAggregations().get("sum_price");
                subVo.setKey("sumVo");
                subVo.setCount(sum.getValue());
            }
            vo.setVo(subVo);
            objectList.add(vo) ;
        }

        pageResult.setResult(objectList);
    }

    private void convertJsonToObject(SearchHits searchHits,Class clazz,List<Object> results){
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            Object obj =  JsonUtil.toObject(hit.getSourceAsString(),clazz);
            results.add(obj) ;
        }

    }



}
