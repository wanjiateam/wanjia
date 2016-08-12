package com.wanjia.service.impl;

import com.wanjia.service.ShopListService;
import com.wanjia.utils.*;
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
    private static  String shopRoomPriceKeyPrefix = "shop_room_price_" ;

    @Autowired
    ElasticSearchClient elasticSearchClient;

    @Autowired
    RedisClient redisClient ;


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
            //根据景区的id和其他一些查询条件以及排序条件获得店家的列表
            PageResult pageResult = getShopListByResort(indexName,type,resortId,startDate,endDate,sortFields,pageSize,page,clazz,landmarkId,shopName);
            //根据店家的列表获得每个店家在指定日期中的最低价
            if(((List) pageResult.getResult()).size() > 0){
                //根据上一步查询出来的结果 获得每个酒店的id 然后再根据日期 去查寻酒店90天的最低价
                getHotelCheapestPriceByShopId(pageResult,"shop_hotel_lowestprice","price",startDate,endDate);
            }

           return pageResult;
    }

    //根据酒店id和日期获得酒店的最低价
    private void getHotelCheapestPriceByShopId(PageResult pageResult, String indexName, String type,long startDate,long endDate){


        long gap = (endDate -startDate)/ DateUtil.dayMillis;

        //存储店家的id到店家住的详细信息
        Map<Long,ShopHotelListVo>  shopHotelListVoHashMap = new HashMap<Long,ShopHotelListVo>();
        //存储店家的Id
        List<Long> shopIds = new ArrayList<Long>();

        //店家的住房列表
        List<ShopHotelListVo> hotelList = (List<ShopHotelListVo>)pageResult.getResult() ;

        for(ShopHotelListVo vo : hotelList){
            shopIds.add(vo.getShopId()) ;
            shopHotelListVoHashMap.put(vo.getShopId(),vo) ;
        }

        //获取所有店家开始日期的最低价格，然后获得这个最低价的roomid 然后根据roomid去获得这个房间的在一定日期范围内的平均价格
        QueryBuilder queryBuilder =  QueryBuilders.termsQuery("shopId",shopIds) ;
        QueryBuilder dateFilter = QueryBuilders.termQuery("priceDateLongValue",startDate);
        try {
            List hotelPriceVos = elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,dateFilter,indexName,type,HotelPriceVo.class);
            if(hotelPriceVos.size() > 0 ){
                //遍历每一个酒店用户查询起始天的最低价房型，然后获得房型的id 店家的房间价格从redis中查询
                for(Object obj : hotelPriceVos){

                    HotelPriceVo vo = (HotelPriceVo) obj ;
                    Long shopId = vo.getShopId();
                    Long roomId = vo.getRoomId() ;
                    //形成这个店家特性放行在redis中的key
                    String key =  shopRoomPriceKeyPrefix+shopId+"_"+roomId ;

                    Map<String,String> roomPrices = redisClient.getAllHashValue(key) ;
                    List<String> dateStrList = DateUtil.getDateList(startDate,endDate) ;

                    double totalPrice = getRoomTotalPriceDuringDateRange(dateStrList,roomPrices);

                    //获得房型价格在查询日期中的平均值
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

        long gap = (endDate -startDate)/DateUtil.dayMillis ;

        //存放每个店家最便宜房间对应的在startDate,endDate之间的平均价格
        Map<Long,Double> shopHotelAveragePriceMap = new HashMap<Long,Double>();
        //存放每个店家对应的最便宜的room的id
        Map<Long,Long> shopHotelRoomIdMap = new HashMap<Long,Long>();

        //存放所店家的id
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
                if(pricePageResult.size() > 0 ){
                    List<String> dateStrList = DateUtil.getDateList(startDate,endDate) ;
                    for(Object obj : pricePageResult){
                        HotelPriceVo vo = (HotelPriceVo) obj ;
                        //根据或的shopId 和roomid去查询 用户输入时间范围的均价
                        Long shopId = vo.getShopId();
                        shopIds.add(shopId) ;
                        Long roomId = vo.getRoomId() ;

                        String key =  shopRoomPriceKeyPrefix+shopId+"_"+roomId ;

                        Map<String,String> roomPrices = redisClient.getAllHashValue(key) ;
                        double totalPrice = -1;
                        //判断店家对应房型在redis中的价格是不是存在，如果不存在则价格为-1 表示异常
                        if(roomPrices.size() >0){
                             totalPrice = getRoomTotalPriceDuringDateRange(dateStrList,roomPrices);
                             shopHotelAveragePriceMap.put(shopId,Math.ceil(totalPrice/gap));
                        }else{
                            //redis不存在对应房型的价格数据
                            shopHotelAveragePriceMap.put(shopId,totalPrice);
                        }

                        shopHotelRoomIdMap.put(shopId,roomId) ;
                    }

                    QueryBuilder shopInfoQueryBuilder = QueryBuilders.termsQuery("shopId",shopIds) ;

                    List shopInfoList = elasticSearchClient.queryDataFromEsWithoutPaging(shopInfoQueryBuilder,null,"shop_hotel","hotel",ShopHotelListVo.class) ;
                    List<ShopHotelListVo> shopHotelListVoList = new ArrayList<ShopHotelListVo>();

                    for(Object obj : shopInfoList){

                        ShopHotelListVo shopHotelListVo = (ShopHotelListVo) obj ;
                        shopHotelListVo.setCheapestPrice(shopHotelAveragePriceMap.get(shopHotelListVo.getShopId()));
                        shopHotelListVo.setRoomId(shopHotelRoomIdMap.get(shopHotelListVo.getShopId()));
                        shopHotelListVoList.add(shopHotelListVo) ;

                    }

                    Collections.sort(shopHotelListVoList);

                    //根据第一个排序的字段对最后的结果排序
                    SortField sortField = sortFields.get(0);
                    if(sortField != null){
                        if(sortField.getSortOrder().equals(SortOrder.DESC)){
                            Collections.reverse(shopHotelListVoList);
                        }
                    }
                    pageResult.setResult(shopHotelListVoList);
                }

        } catch (Exception e) {
            logger.error("get data from es error",e);
            pageResult.setE(e);
        }
        return  pageResult;
    }

   //获取一段时间内住房的总价格
    private double getRoomTotalPriceDuringDateRange( List<String> dateStrList, Map<String,String> roomPrices){
        double totalPrice = 0 ;
        String price = null ;
        if(roomPrices.size() >0){

        }
        for(int i=0 ; i< dateStrList.size() -1; i++){
            String dateStr = dateStrList.get(i) ;
            price  = roomPrices.get(dateStr) ;
            if(price == null){
                boolean isWeekend = DateUtil.isWeekend(dateStr);
                if(isWeekend){
                    String weekendPrice = roomPrices.get("weekend") ;
                    if(weekendPrice != null){
                        price = weekendPrice;
                    }else{
                        price = roomPrices.get("normal") ;
                    }
                }else {
                    price = roomPrices.get("normal") ;
                }
            }
            totalPrice += Double.valueOf(price) ;
        }
        return totalPrice ;
    }

    //根据点评，推荐等非价格的排序方式获得所有的店家，然后在根据店家的id去获得不同的价格
    @Override
    public PageResult getShopTravelListByResort(String indexName, String type, long resortId, long startDate, long endDate, List<SortField> sortFields, int pageSize, int page, Class clazz, Integer landmarkId,String shopName) {

        PageResult pageResult = getShopListByResort(indexName,type,resortId,startDate,endDate,sortFields,pageSize,page,clazz,landmarkId,shopName);
        getShopTravelShowPrice(pageResult,"shop_ticket_price_");
        getShopTravelShowPrice(pageResult,"shop_guide_price_");
        getShopTravelShowPrice(pageResult,"shop_familyactivity_price_");

        return pageResult;
    }


    /**
     *
     * @param pageResult
     * @param prefixKey 门票 导游 农家自助游 价格在redis中的key的前缀
     */
    //一级界面展示的时候 店家游价格的获取方法（包括 导游 门票 农家自助游）
    private  void getShopTravelShowPrice(PageResult pageResult,String prefixKey){

        List<ShopTravelListVo> travelList = (List<ShopTravelListVo>)pageResult.getResult() ;
        for(ShopTravelListVo shopTravelListVo : travelList){
            long shopId = shopTravelListVo.getShopId() ;
            String key = prefixKey+shopId ;
            //获得店家所有的票价信息
            Map<String,String> mapValues = redisClient.getAllHashValue(key) ;
            if(mapValues != null && mapValues.size() >0){
                //如果存在普通票的价格就按照普通票价返回
                if(mapValues.get("normal") != null){
                    shopTravelListVo.setTravelTicketLowestPrice(Double.valueOf(mapValues.get("normal")));
                }else{
                    //如果不存在普通票的价格那么就按照所有票价中最低的票价返回
                    List<Double> allPriceValue = new ArrayList<Double>();
                    Set<Map.Entry<String,String>> entries = mapValues.entrySet() ;
                    for(Map.Entry<String,String> entry : entries){
                        allPriceValue.add(Double.valueOf(entry.getValue())) ;
                    }
                    Collections.sort(allPriceValue);
                    shopTravelListVo.setTravelTicketLowestPrice(allPriceValue.get(0));
                }
            }
        }
    }


/*

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
*/


    /**
     * 根据信息获得特定景区的店家列表
     * @param indexName
     * @param type
     * @param resortId
     * @param startDate
     * @param endDate
     * @param sortFields
     * @param pageSize
     * @param page
     * @param clazz
     * @param landmarkId
     * @param shopName
     * @return
     */
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
            //根据resortId获得景区的店家列表
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
    public PageResult getShopListByDistance(String indexName, String type, long resortId, List<SortField> sortFields, Class clazz ,int pageSize, int page, int productType, double lon, double lat,long startDate,long endDate) {

        PageResult pageResult = new PageResult();
        pageResult.setPageNum(page);
        pageResult.setPageSize(pageSize);

        int from = (page-1)*pageSize ;

        QueryBuilder queryBuilder1 = QueryBuilders.termQuery("resortId",resortId);
        QueryBuilder geoQuery = QueryBuilders.geoDistanceQuery("location").lon(lon).lat(lat).distance(10, DistanceUnit.KILOMETERS).geoDistance(GeoDistance.PLANE) ;
        //按照geo距离排序
       GeoDistanceSortBuilder geoDistanceSortBuilder =  SortBuilders.geoDistanceSort("location").order(SortOrder.ASC).point(lat,lon).unit(DistanceUnit.KILOMETERS) ;
        try {
             elasticSearchClient.queryDataFromEsWithPostFilter(queryBuilder1,geoQuery,sortFields,geoDistanceSortBuilder ,indexName,type,from,pageSize,clazz,pageResult);
             if(productType ==  1){
                 getHotelCheapestPriceByShopId(pageResult,"shop_hotel_lowestprice","price",startDate,endDate);
             }else if(productType == 3){
                 getShopTravelShowPrice(pageResult,"shop_ticket_price_");
                 getShopTravelShowPrice(pageResult,"shop_guide_price_");
                 getShopTravelShowPrice(pageResult,"shop_familyactivity_price_");
             }
        } catch (Exception e) {
            logger.error("get data from es error",e);
            pageResult.setE(e);
        }
        return pageResult;
    }
}
