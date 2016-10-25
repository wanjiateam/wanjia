package com.wanjia.utils;

import com.wanjia.exceptions.ElasticSearchException;
import com.wanjia.vo.ESAggResultVo;
import org.apache.ibatis.ognl.ObjectElementsAccessor;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.collect.HppcMaps;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.engine.VersionConflictEngineException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public List queryDataFromEsWithoutPaging(QueryBuilder queryBuilder, QueryBuilder postFilter,  String index, String type, Class clazz) throws ElasticSearchException{

        return this.queryDataFromEsWithoutPaging(queryBuilder, postFilter, index,type,clazz,null) ;

    }


    public List queryDataFromEsWithoutPaging(QueryBuilder queryBuilder, QueryBuilder postFilter,  String index, String type, Class clazz,List<SortField>  sortFields) throws ElasticSearchException{

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type).setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(queryBuilder).setFrom(0).setSize(10000);//max_result_window

        if(postFilter != null ){
            searchRequestBuilder.setPostFilter(postFilter) ;
        }
        if(sortFields != null && sortFields.size() >0){
            for(SortField sortField : sortFields){
                searchRequestBuilder.addSort(sortField.getField(),sortField.getSortOrder());
            }
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


    public void queryDataFromEsWithPostFilter(QueryBuilder queryBuilder, QueryBuilder postFilter , List<SortField> sortFields, GeoDistanceSortBuilder geoDistanceSortBuilder , String index, String type, int from, int size, Class clazz, PageResult pageResult) throws ElasticSearchException{

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


    public Map<String,Object> queryUniqueColumnSpecificField(QueryBuilder queryBuilder, QueryBuilder postFilter , List<String> fields , String index, String type) throws ElasticSearchException{

        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(index).setTypes(type).setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(queryBuilder);

        Map<String,Object> fieldValueMap = new HashMap<String,Object>();

        for(String field : fields){
            searchRequestBuilder.addField(field);
        }

        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        SearchHits searchHits = searchResponse.getHits();
        for(SearchHit hit : searchHits.getHits()){
            for(String field : fields){
                fieldValueMap.put(field,hit.field(field).getValue());
            }
        }
        return fieldValueMap ;
    }


    public void queryDataFromEsWithPostFilterAndAggregation(QueryBuilder queryBuilder, QueryBuilder postFilter, AggregationBuilder aggregationBuilder,String groupName,String subAggName,
                                                            String aggType , String index, String type, int from, int size, PageResult pageResult) throws ElasticSearchException{

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


    public boolean checkEntityExist(String index,String type,String id) throws ElasticSearchException{

        GetRequest getRequest = new GetRequest(index,type,id);
        GetResponse getResponse =  client.get(getRequest).actionGet() ;

        return getResponse.isExists() ;

    }


    public Map<String,Object> getEntityById(String index,String type,String id) throws ElasticSearchException{

        GetRequest getRequest = new GetRequest(index,type,id);

        GetResponse getResponse =  client.get(getRequest).actionGet() ;
        return getResponse.getSource() ;
    }

    public GetResponse getRequestExecute(GetRequest getRequest) throws ElasticSearchException{

        GetResponse getResponse =  client.get(getRequest).actionGet() ;

        return getResponse ;
    }

    public void executeUpdateRequest(UpdateRequest updateRequest)throws  VersionConflictEngineException,ElasticSearchException{

        try {
            client.update(updateRequest).actionGet() ;
        } catch (VersionConflictEngineException e) {
            throw e ;
        }catch (Exception e){
            throw new ElasticSearchException("es exception",e) ;
        }

    }

}
