package com.wanjia.utils;

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



    public void queryDataFromEsWithPostFilter(QueryBuilder queryBuilder, QueryBuilder postFilter, List<SortField> sortFields, GeoDistanceSortBuilder geoDistanceSortBuilder , String index, String type, int from, int size, Class clazz, PageResult pageResult) throws Exception{

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
        pageResult.setTotalNumber(totalHits);

        List objectList = new ArrayList() ;
        if (totalHits > 0) {
            convertJsonToObject(searchHits,clazz,objectList);
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
