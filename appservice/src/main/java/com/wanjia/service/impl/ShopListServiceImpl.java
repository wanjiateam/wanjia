package com.wanjia.service.impl;

import com.wanjia.service.ShopListService;
import com.wanjia.utils.ElasticSearchClient;
import com.wanjia.utils.PageResult;
import com.wanjia.utils.SortField;
import com.wanjia.vo.*;
import org.apache.log4j.Logger;
import org.elasticsearch.action.bulk.BulkAction;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
     * 根据景区id获取住的店家列表，排序方式为好评，推荐和默认
     * @param indexName
     * @param type
     * @param resortId
     * @param startDate
     * @param endDate
     * @param sortFields
     * @param pageSize
     * @param page
     * @param landmarkId 景区的地标id
     * @param shopName 店家的名字
     * @return
     */
    @Override
    public PageResult getShopHotelListByResort(String indexName, String type, long resortId, long startDate, long endDate , List<SortField> sortFields, int pageSize, int page,Class clazz ,Integer landmarkId,String shopName) {

            PageResult pageResult = getShopListByResort(indexName,type,resortId,startDate,endDate,sortFields,pageSize,page,clazz,landmarkId,shopName);
            if(((List) pageResult.getResult()).size() > 0){
                //根据上一步查询出来的结果 或的每个酒店的id 然后再根据日期 去查寻酒店90天的最低价
                getHotelCheapestPriceByShopId(pageResult,"shop_hotel_lowestprice","price",startDate,endDate);
            }

           return pageResult;
    }

    //根据就id或日期获得酒店90天的最低价
    private void getHotelCheapestPriceByShopId(PageResult pageResult, String indexName, String type,long startDate,long endDate){

        long gap = (endDate -startDate)/(24 * 60 * 60 * 1000) ;

        Map<Long,ShopHotelListVo>  shopHotelListVoHashMap = new HashMap<Long,ShopHotelListVo>();
        List<Long> shopIds = new ArrayList<Long>();

        List<ShopHotelListVo> hotelList = (List<ShopHotelListVo>)pageResult.getResult() ;

        for(ShopHotelListVo vo : hotelList){
            shopIds.add(vo.getShopId()) ;
            shopHotelListVoHashMap.put(vo.getShopId(),vo) ;
        }

        QueryBuilder queryBuilder =  QueryBuilders.termsQuery("shopId",shopIds) ;
        QueryBuilder dateFilter = QueryBuilders.termQuery("priceDateLongValue",startDate);
        try {
            List hotelPriceVos = elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,dateFilter,indexName,type,HotelPriceVo.class);
            if(hotelPriceVos.size() > 0 ){

                for(Object obj : hotelPriceVos){
                    HotelPriceVo vo = (HotelPriceVo) obj ;
                    Long shopId = vo.getShopId();
                    Long roomId = vo.getRoomId() ;
                    BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery() ;
                    boolQueryBuilder.must(QueryBuilders.termQuery("shopId",shopId)).must(QueryBuilders.termQuery("roomId",roomId)) ;
                    QueryBuilder postDateFilter = QueryBuilders.rangeQuery("priceDateLongValue").gte(startDate).lt(endDate) ;
                    List priceList = elasticSearchClient.queryDataFromEsWithoutPaging(boolQueryBuilder,postDateFilter,"shop_hotel_price","price",HotelPriceVo.class);

                    double totalPrice = 0 ;
                    for(Object priceVo : priceList){
                        HotelPriceVo hpo = (HotelPriceVo) priceVo ;
                        totalPrice+=hpo.getRoomPrice() ;
                    }
                    shopHotelListVoHashMap.get(shopId).setCheapestPrice(Math.ceil(totalPrice/gap));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 按照低价优先的顺序获得店家的列表（按照开始日期的价格作为排序价格，然后获取店铺的id，然后根据id再去获取店铺的详细信息）
     * @param indexName
     * @param type
     * @param resortId
     * @param startDate
     * @param endDate
     * @param sortFields
     * @param pageSize
     * @param page
     * @return
     */
    @Override
    public PageResult getShopHotelListPriceLowFirstByResort(String indexName, String type, long resortId, long startDate, long endDate, List<SortField> sortFields, int pageSize, int page,Class clazz,Integer landmarkId,String shopName) {

        long gap = (endDate -startDate)/(24 * 60 * 60 * 1000) ;

        Map<Long,Double> shopHotelAveragePriceMap = new HashMap<Long,Double>();
        Map<Long,Long> shopHotelRoomIdMap = new HashMap<Long,Long>();

        List<Long>  shopIds = new ArrayList<Long>() ;
        PageResult pageResult = new PageResult();
        pageResult.setPageNum(page);
        pageResult.setPageSize(pageSize);

        int from = (page-1)*pageSize ;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder1 = null ;
        QueryBuilder shopNameQueryBuilder = null ;

        if(landmarkId != null){
            //landmarkId要求唯一
            queryBuilder1 = QueryBuilders.termQuery("landmarkId",landmarkId.intValue()) ;
        }else{
            queryBuilder1 = QueryBuilders.termQuery("resortId",resortId) ;
        }
        boolQueryBuilder.must(queryBuilder1) ;
        if(shopName != null ){
            shopNameQueryBuilder = QueryBuilders.matchQuery("shopName",shopName);
            boolQueryBuilder.must(shopNameQueryBuilder) ;
        }

        List pricePageResult = null ;

        try {

            //根据第一天的房价来最低价来得到来确定排序
                QueryBuilder queryBuilder2 = QueryBuilders.termQuery("priceDateLongValue",startDate) ;
                boolQueryBuilder.must(queryBuilder2) ;
                elasticSearchClient.queryDataFromEsWithPostFilter(boolQueryBuilder,null,sortFields,null,indexName,type,from,pageSize,clazz,pageResult);

                pricePageResult = (List) pageResult.getResult() ;

               for(Object obj : pricePageResult){
                   HotelPriceVo vo = (HotelPriceVo) obj ;
                   //根据或的shopId 和roomid去查询 用户输入时间范围的均价
                   Long shopId = vo.getShopId();
                   shopIds.add(shopId) ;
                   Long roomId = vo.getRoomId() ;
                   BoolQueryBuilder boolQueryBuilder1 = QueryBuilders.boolQuery() ;
                   boolQueryBuilder1.must(QueryBuilders.termQuery("shopId",shopId)).must(QueryBuilders.termQuery("roomId",roomId)) ;
                   QueryBuilder postDateFilter = QueryBuilders.rangeQuery("priceDateLongValue").gte(startDate).lt(endDate) ;
                   List priceList = elasticSearchClient.queryDataFromEsWithoutPaging(boolQueryBuilder1,postDateFilter,"shop_hotel_price","price",HotelPriceVo.class);

                   double totalPrice = 0 ;
                   for(Object priceVo : priceList){
                       HotelPriceVo hpo = (HotelPriceVo) priceVo ;
                       totalPrice+=hpo.getRoomPrice() ;
                   }
                   shopHotelAveragePriceMap.put(shopId,Math.ceil(totalPrice/gap));
                   shopHotelRoomIdMap.put(shopId,roomId) ;
               }

            QueryBuilder shopInfoQueryBuilder = QueryBuilders.termsQuery("shopId",shopIds) ;

            List shopInfoList = elasticSearchClient.queryDataFromEsWithoutPaging(shopInfoQueryBuilder,null,"shop_hotel","hotel",ShopHotelListVo.class) ;
            List<ShopHotelListVo> shopHotelListVoList = new ArrayList<ShopHotelListVo>();
            for(Object obj : shopInfoList){
                ShopHotelListVo shopHotelListVo = (ShopHotelListVo) obj ;
                shopHotelListVoList.add(shopHotelListVo) ;
                shopHotelListVo.setCheapestPrice(shopHotelAveragePriceMap.get(shopHotelListVo.getShopId()));
                shopHotelListVo.setRoomId(shopHotelRoomIdMap.get(shopHotelListVo.getShopId()));
            }

            Collections.sort(shopHotelListVoList);

            pageResult.setResult(shopHotelListVoList);

        } catch (Exception e) {
            logger.error("get data from es error",e);
            pageResult.setE(e);
        }
        return  pageResult;
    }

    @Override
    public PageResult getShopTravelListByResort(String indexName, String type, long resortId, long startDate, long endDate, List<SortField> sortFields, int pageSize, int page, Class clazz, Integer landmarkId,String shopName) {

        PageResult pageResult = getShopListByResort(indexName,type,resortId,startDate,endDate,sortFields,pageSize,page,clazz,landmarkId,shopName);
        getTravelTicketLowestPriceByShopId(pageResult,"shop_travel_ticketlowestprice","price",startDate,endDate);
        return pageResult;
    }

    //根据shopIds 获得店家的最低价
    private void getTravelTicketLowestPriceByShopId(PageResult pageResult, String indexName, String type,long startDate,long endDate){

        long gap = (endDate -startDate)/(24 * 60 * 60 * 1000) ;

        Map<Long,Double> shopTicketAvgPriceMap = new HashMap<Long,Double>();

        List<Long> shopIds = new ArrayList<Long>();
        List<ShopTravelListVo> travelList = (List<ShopTravelListVo>)pageResult.getResult() ;

        for(ShopTravelListVo vo : travelList){
            shopIds.add(vo.getShopId()) ;
        }

        QueryBuilder queryBuilder =  QueryBuilders.termsQuery("shopId",shopIds) ;
        QueryBuilder dateFilter = QueryBuilders.termQuery("priceDateLongValue",startDate);
        try {
            List ticketPriceVos = elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,dateFilter,indexName,type,TicketPriceVo.class);
            if(ticketPriceVos.size() > 0 ){
                String shopTravelTicketIndexName = "shop_travel_ticketprice" ; ;
                String shopTravelTicketType = "price" ;

                for(Object obj : ticketPriceVos){
                    TicketPriceVo vo = (TicketPriceVo) obj ;
                    Long shopId = vo.getShopId() ;
                    Long ticketId = vo.getTicketId() ;

                    QueryBuilder priceQueryBuilder = QueryBuilders.boolQuery().must(QueryBuilders.termQuery("shopId",shopId)).must(QueryBuilders.termQuery("ticketId",ticketId)) ;
                    QueryBuilder pricePostFilter = QueryBuilders.rangeQuery("priceDateLongValue").gte(startDate).lt(endDate) ;
                    List dateRangePrice = elasticSearchClient.queryDataFromEsWithoutPaging(priceQueryBuilder,pricePostFilter,shopTravelTicketIndexName,shopTravelTicketType,TicketPriceVo.class) ;

                    double totalPrice = 0 ;
                    for(Object objPriceVo : dateRangePrice){
                        TicketPriceVo ticketPriceVo = (TicketPriceVo) objPriceVo ;
                        totalPrice+=ticketPriceVo.getPrice() ;
                    }

                    shopTicketAvgPriceMap.put(shopId,Math.ceil(totalPrice/gap)) ;                }
            }
            for(ShopTravelListVo vo : travelList){
                vo.setTravelTicketLowestPrice(shopTicketAvgPriceMap.get(vo.getShopId()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public PageResult getShopTravelListTicketPriceLowFirstByResort(String indexName, String type, long resortId, long startDate, long endDate, List<SortField> sortFields, int pageSize, int page,Class clazz,Integer landmarkId,String shopName) {

        long gap = (endDate -startDate)/(24 * 60 * 60 * 1000) ;

        Map<Long,Double> shopTicketAvgPriceMap = new HashMap<Long,Double>();
        Map<Long,Long> shopIdTicketIdMap = new HashMap<Long,Long>();

        List<Long>  shopIds = new ArrayList<Long>() ;
        PageResult pageResult = new PageResult();
        pageResult.setPageNum(page);
        pageResult.setPageSize(pageSize);

        int from = (page-1)*pageSize ;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder1 = null ;
        QueryBuilder shopNameQueryBuilder = null ;

        AggregationBuilder aggregationBuilder = null ;

        if(landmarkId != null){
            //landmarkId要求唯一
            queryBuilder1 = QueryBuilders.termQuery("landmarkId",landmarkId.intValue()) ;
        }else{
            queryBuilder1 = QueryBuilders.termQuery("resortId",resortId) ;
        }
        boolQueryBuilder.must(queryBuilder1) ;
        if(shopName != null ){
            shopNameQueryBuilder = QueryBuilders.matchQuery("shopName",shopName);
            boolQueryBuilder.must(shopNameQueryBuilder) ;
        }

        List pricePageResult = null ;

        try {

               //按照第一天的价格去排序，然后再取得用户查询时间范围内的平均值
                QueryBuilder queryBuilder2 = QueryBuilders.termQuery("priceDateLongValue",startDate) ;
                boolQueryBuilder.must(queryBuilder2) ;
                elasticSearchClient.queryDataFromEsWithPostFilter(boolQueryBuilder,null,sortFields,null,indexName,type,from,pageSize,clazz,pageResult);

                pricePageResult = (List) pageResult.getResult() ;

                String ticketPriceIndexName = "shop_travel_ticketprice";
                String ticketPriceType = "price";
                for(Object obj : pricePageResult){
                    TicketPriceVo ticketLowestPriceVo = (TicketPriceVo) obj ;
                    Long shopId = ticketLowestPriceVo.getShopId() ;

                    shopIds.add(shopId);

                    Long ticketId = ticketLowestPriceVo.getTicketId() ;
                    QueryBuilder ticketPriceQueryBuilder = QueryBuilders.boolQuery().
                             must(QueryBuilders.termQuery("shopId",shopId)).must(QueryBuilders.termQuery("ticketId",ticketId));

                    QueryBuilder dateRangeQuery = QueryBuilders.rangeQuery("priceDateLongValue").gte(startDate).lt(endDate) ;

                    List ticketPriceList = elasticSearchClient.queryDataFromEsWithoutPaging(ticketPriceQueryBuilder,dateRangeQuery,ticketPriceIndexName,ticketPriceType,TicketPriceVo.class) ;

                    double totalPrice = 0 ;
                    for(Object objTicketPrice : ticketPriceList){
                        TicketPriceVo tpo = (TicketPriceVo) obj ;
                        totalPrice += tpo.getPrice() ;
                    }
                    shopTicketAvgPriceMap.put(shopId,Math.ceil(totalPrice/gap));
                    shopIdTicketIdMap.put(shopId,ticketId);
                }

            //根据shopId去查询所有的店家
            List ticketPriceList =null;
            if(shopIds.size() > 0){
                //根据上一步查询出来的结果 的每个酒店的id 然然后获去酒店的详细信息
                QueryBuilder idsQuery  = QueryBuilders.termsQuery("shopId",shopIds);
                ticketPriceList =  elasticSearchClient.queryDataFromEsWithoutPaging(idsQuery,null,"shop_travel","travel",ShopTravelListVo.class);
            }

            if(ticketPriceList !=null && ticketPriceList.size() >0){
                for(Object obj : ticketPriceList){
                    ShopTravelListVo stlo = (ShopTravelListVo) obj ;
                    stlo.setTravelTicketLowestPrice(shopTicketAvgPriceMap.get(stlo.getShopId()));
                }
            }

            Collections.sort(ticketPriceList);
            pageResult.setResult(ticketPriceList);

        } catch (Exception e) {
            logger.error("get data from es error",e);
            pageResult.setE(e);
        }
        return  pageResult;
    }




    @Override
    public PageResult getShopListByResort(String indexName, String type, long resortId, long startDate, long endDate, List<SortField> sortFields, int pageSize, int page,Class clazz,Integer landmarkId,String shopName) {

        PageResult pageResult = new PageResult();
        pageResult.setPageNum(page);
        pageResult.setPageSize(pageSize);

        int from = (page-1)*pageSize ;
        QueryBuilder queryBuilder1 = null ;
        if(landmarkId != null ){
            queryBuilder1 = QueryBuilders.termQuery("landmarkId",landmarkId.intValue()) ;
        }else{
            queryBuilder1 = QueryBuilders.termQuery("resortId",resortId) ;
        }

        QueryBuilder queryBuilder2 = null ;

        if(shopName != null){
            queryBuilder2 = QueryBuilders.matchQuery("shopName",shopName) ;
        }


        try {
            //根据resortId获得景区的饭店列表
            elasticSearchClient.queryDataFromEsWithPostFilter(queryBuilder1,queryBuilder2 ,sortFields,null ,indexName,type,from,pageSize,clazz,pageResult);
        } catch (Exception e) {
            logger.error("get data from es error",e);
            pageResult.setE(e);
        }
        return  pageResult;
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
        QueryBuilder geoQuery = QueryBuilders.geoDistanceQuery("location").lon(lon).lat(lat).distance(10, DistanceUnit.KILOMETERS).geoDistance(GeoDistance.PLANE) ;
        //按照geo距离排序
       GeoDistanceSortBuilder geoDistanceSortBuilder =  SortBuilders.geoDistanceSort("location").order(SortOrder.ASC).point(lat,lon).unit(DistanceUnit.KILOMETERS) ;
        try {
             elasticSearchClient.queryDataFromEsWithPostFilter(queryBuilder1,geoQuery,sortFields,geoDistanceSortBuilder ,indexName,type,from,pageSize,ShopListBaseVo.class,pageResult);
        } catch (Exception e) {
            logger.error("get data from es error",e);
            pageResult.setE(e);
        }
        return pageResult;
    }
}
