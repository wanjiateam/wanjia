package com.wanjia.service.impl;

import com.wanjia.service.ShopListService;
import com.wanjia.utils.ElasticSearchClient;
import com.wanjia.utils.PageResult;
import com.wanjia.utils.SortField;
import com.wanjia.vo.ShopListVo;
import org.apache.log4j.Logger;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by blake on 2016/6/22.
 * 展示用户按照地区搜索的店家结果
 */
@Service("shopListService")
public class ShopListServiceImpl implements ShopListService{

    private static Logger logger = Logger.getLogger(ShopListServiceImpl.class) ;
    @Autowired
    ElasticSearchClient elasticSearchClient;

    /**
     *
     * @param indexName 索引名称
     * @param type 索引的type
     * @param resortId 景区的id
     * @param sortFields 排序字段名称
     * @param pageSize 一次返回多少条数据
     * @param page 查询那一页数据
     * @param productType 表示查询那个类型的数据 1住2食3游4产
     * @return 分页后的数据
     */
    @Override
    public PageResult getShopListByResort(String indexName, String type , long resortId, List<SortField> sortFields, int pageSize, int page, int productType) {

        PageResult pageResult = new PageResult();
        pageResult.setPageNum(page);
        pageResult.setPageSize(pageSize);

        int from = (page-1)*pageSize ;

        QueryBuilder queryBuilder1 = QueryBuilders.termQuery("resortId",resortId);
        QueryBuilder queryBuilder2 ;
        switch (productType) {
            case 1 : queryBuilder2 = QueryBuilders.termQuery("liveState",1);break;//查询提供住服务的店家
            case 2 : queryBuilder2 = QueryBuilders.termQuery("foodState",1);break;//查询提供食服务的店家
            case 3 : queryBuilder2 = QueryBuilders.termQuery("travelTicketState",1);break;//查询提供门票服务的店家
            case 4 : queryBuilder2 = QueryBuilders.termQuery("travelGuideState",1);break;//查询提供导游服务的店家
            case 5 : queryBuilder2 = QueryBuilders.termQuery("travelSpecialState",1);break;//查询提供自助游服务的店家
            case 6 : queryBuilder2 = QueryBuilders.termQuery("specialState",1);break;//查询提供特产服务的店家
            default: queryBuilder2 = QueryBuilders.matchAllQuery() ;
        }
        try {
            elasticSearchClient.queryDataFromEsWithPostFilter(queryBuilder1,queryBuilder2,sortFields,null ,indexName,type,from,pageSize,ShopListVo.class,pageResult);
        } catch (Exception e) {
            logger.error("get data from es error",e);
            pageResult.setE(e);
        }

        return pageResult;
    }

    @Override
    public PageResult getShopListByLandmark(String indexName, String type, long resortId, List<SortField> sortFields, int pageSize, int page, int productType,int landmarkId) {

        PageResult pageResult = new PageResult();
        pageResult.setPageNum(page);
        pageResult.setPageSize(pageSize);

        int from = (page - 1) * pageSize;

        //根据landmarkid定位到所有的店家
        QueryBuilder queryBuilder1 = QueryBuilders.termQuery("landmarkId", landmarkId);
        //根据resortId过滤一次 可以不使用这个条件 因为目前我们是按照不同的地标id不同来的
        QueryBuilder queryBuilder2 ;
        switch (productType) {
            case 1 : queryBuilder2 = QueryBuilders.termQuery("liveState",1);break;//查询提供住服务的店家
            case 2 : queryBuilder2 = QueryBuilders.termQuery("foodState",1);break;//查询提供食服务的店家
            case 3 : queryBuilder2 = QueryBuilders.termQuery("travelTicketState",1);break;//查询提供门票服务的店家
            case 4 : queryBuilder2 = QueryBuilders.termQuery("travelGuideState",1);break;//查询提供导游服务的店家
            case 5 : queryBuilder2 = QueryBuilders.termQuery("travelSpecialState",1);break;//查询提供自助游服务的店家
            case 6 : queryBuilder2 = QueryBuilders.termQuery("specialState",1);break;//查询提供特产服务的店家
            default: queryBuilder2 = QueryBuilders.matchAllQuery() ;
        }
        try {
            elasticSearchClient.queryDataFromEsWithPostFilter(queryBuilder1, queryBuilder2, sortFields, null ,indexName, type, from, pageSize, ShopListVo.class, pageResult);
        } catch (Exception e) {
            logger.error("get data from es error", e);
            pageResult.setE(e);
        }

        return pageResult;
    }


    /**
     *
     * @param indexName
     * @param type
     * @param resortId
     * @param sortFields
     * @param pageSize
     * @param page
     * @param productType
     * @param lon 经度
     * @param lat 维度
     * @return
     */
    @Override
    public PageResult getShopListByDistance(String indexName, String type, long resortId, List<SortField> sortFields, int pageSize, int page, int productType, double lon, double lat) {

        PageResult pageResult = new PageResult();
        pageResult.setPageNum(page);
        pageResult.setPageSize(pageSize);

        int from = (page-1)*pageSize ;

        QueryBuilder queryBuilder1 = QueryBuilders.termQuery("resortId",resortId);
        QueryBuilder queryBuilder2 ;
        switch (productType) {
            case 1 : queryBuilder2 = QueryBuilders.termQuery("liveState",1);break;//查询提供住服务的店家
            case 2 : queryBuilder2 = QueryBuilders.termQuery("foodState",1);break;//查询提供食服务的店家
            case 3 : queryBuilder2 = QueryBuilders.termQuery("travelTicketState",1);break;//查询提供门票服务的店家
            case 4 : queryBuilder2 = QueryBuilders.termQuery("travelGuideState",1);break;//查询提供导游服务的店家
            case 5 : queryBuilder2 = QueryBuilders.termQuery("travelSpecialState",1);break;//查询提供自助游服务的店家
            case 6 : queryBuilder2 = QueryBuilders.termQuery("specialState",1);break;//查询提供特产服务的店家
            default: queryBuilder2 = QueryBuilders.matchAllQuery() ;
        }
        QueryBuilder geoQuery = QueryBuilders.geoDistanceQuery("location").lat(lat).lon(lon).distance(10, DistanceUnit.KILOMETERS).geoDistance(GeoDistance.PLANE) ;
        QueryBuilder booleanQuery = QueryBuilders.boolQuery().must(queryBuilder2).must(geoQuery) ;
        //按照geo距离排序
       GeoDistanceSortBuilder geoDistanceSortBuilder =  SortBuilders.geoDistanceSort("location").order(SortOrder.ASC).point(lon,lat).unit(DistanceUnit.KILOMETERS) ;

        try {
             elasticSearchClient.queryDataFromEsWithPostFilter(queryBuilder1,booleanQuery,sortFields,geoDistanceSortBuilder ,indexName,type,from,pageSize,ShopListVo.class,pageResult);
        } catch (Exception e) {
            logger.error("get data from es error",e);
            pageResult.setE(e);
        }
        return pageResult;
    }
}
