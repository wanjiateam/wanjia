package com.wanjia.service.impl;

import com.wanjia.service.ShopListService;
import com.wanjia.utils.ElasticSearchClient;
import com.wanjia.utils.PageResult;
import com.wanjia.utils.SortField;
import com.wanjia.vo.*;
import org.apache.log4j.Logger;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
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
                getHotelCheapestPriceByShopId(pageResult,"shop_hotel_price","price",startDate,endDate);
            }

           return pageResult;
    }

    //根据就id或日期获得酒店90天的最低价
    private void getHotelCheapestPriceByShopId(PageResult pageResult, String indexName, String type,long startDate,long endDate){

        Map<Long,HotelPriceVo> houseLowestPrice = new HashMap<Long,HotelPriceVo>();
        List<Long> shopIds = new ArrayList<Long>();

        List<ShopHotelListVo> hotelList = (List<ShopHotelListVo>)pageResult.getResult() ;

        for(ShopHotelListVo vo : hotelList){
            shopIds.add(vo.getShopId()) ;
        }

        //要求roomId全局唯一
        QueryBuilder queryBuilder =  QueryBuilders.termsQuery("shopId",shopIds) ;
        QueryBuilder dateFilter = QueryBuilders.rangeQuery("priceDateLongValue").gte(startDate).lt(endDate) ;
        try {
            List hotelPriceVos = elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,dateFilter,indexName,type,HotelPriceVo.class);
            if(hotelPriceVos.size() > 0 ){
                for(Object obj : hotelPriceVos){
                    HotelPriceVo vo = (HotelPriceVo) obj ;
                    HotelPriceVo hotelPriceVo = houseLowestPrice.get(vo.getRoomId());
                    if(hotelPriceVo == null){
                        houseLowestPrice.put(vo.getRoomId(),vo);
                    }else{
                        if(hotelPriceVo.getRoomPrice() > vo.getRoomPrice()){
                            houseLowestPrice.put(vo.getRoomId(),vo);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(ShopHotelListVo vo : hotelList){
            HotelPriceVo hotelPriceVo =  houseLowestPrice.get(vo.getRoomId()) ;
            if(hotelPriceVo != null){
                vo.setCheapestPrice(hotelPriceVo.getRoomPrice()) ;
                vo.setCheapestDate(hotelPriceVo.getPriceDate());
            }
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

        Map<Long,ShopHotelListVo> shopHotelListVoHashMap = new HashMap<Long,ShopHotelListVo>();
        List<Long> roomIds = new ArrayList<Long>() ;
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
        QueryBuilder queryBuilder2 = QueryBuilders.termQuery("priceDateLongValue",startDate) ;
        boolQueryBuilder.must(queryBuilder1).must(queryBuilder2) ;

        if(shopName != null ){
            shopNameQueryBuilder = QueryBuilders.matchQuery("shopName",shopName);
            boolQueryBuilder.must(shopNameQueryBuilder) ;
        }
        try {
            //根据resortId和预定日期的第一天 去按照价格低价的顺序去获取店家id的列表
            elasticSearchClient.queryDataFromEsWithPostFilter(boolQueryBuilder,null ,sortFields,null ,indexName,type,from,pageSize,clazz,pageResult);
            List hotelPriceVos = (List) pageResult.getResult() ;

            for(Object hotelPriceVo : hotelPriceVos){
                HotelPriceVo hpo = (HotelPriceVo) hotelPriceVo ;
                roomIds.add(hpo.getRoomId()) ;
            }


            if(hotelPriceVos.size() > 0){
                //根据上一步查询出来的结果 的每个酒店的id 然然后获去酒店的详细信息
                QueryBuilder idsQuery  = QueryBuilders.termsQuery("roomId",roomIds);
                elasticSearchClient.queryDataFromEsWithPostFilter(idsQuery,null ,null,null ,"shop_hotel","hotel",0,roomIds.size(),ShopHotelListVo.class,pageResult);
            }

            List shopListVos = (List) pageResult.getResult() ;
            for(Object obj : shopListVos){
                ShopHotelListVo shlo = (ShopHotelListVo) obj ;
                shopHotelListVoHashMap.put(shlo.getRoomId(),shlo) ;
            }

            List<ShopHotelListVo> shopHotelListVoList = new ArrayList<ShopHotelListVo>() ;
            for(Object hotelPriceVo : hotelPriceVos){
                HotelPriceVo hpo = (HotelPriceVo) hotelPriceVo ;
                ShopHotelListVo shopHotelListVo = shopHotelListVoHashMap.get(hpo.getRoomId());
                shopHotelListVo.setCheapestPrice(hpo.getRoomPrice());
                shopHotelListVo.setCheapestDate(hpo.getPriceDate());
                shopHotelListVo.setShopId(hpo.getShopId());
                shopHotelListVoList.add(shopHotelListVo) ;
            }
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
        getTravelTicketLowestPriceByShopId(pageResult,"shop_travel_ticketprice","price",startDate,endDate);
        return pageResult;
    }

    //根据shopIds 获得店家的最低价
    private void getTravelTicketLowestPriceByShopId(PageResult pageResult, String indexName, String type,long startDate,long endDate){
        Map<Long,TicketPriceVo> ticketLowestPrice = new HashMap<Long,TicketPriceVo>();
        List<Long> shopIds = new ArrayList<Long>();

        List<ShopTravelListVo> travelList = (List<ShopTravelListVo>)pageResult.getResult() ;

        for(ShopTravelListVo vo : travelList){
            shopIds.add(vo.getShopId()) ;
        }

        QueryBuilder queryBuilder =  QueryBuilders.termsQuery("shopId",shopIds) ;
        QueryBuilder dateFilter = QueryBuilders.rangeQuery("priceDateLongValue").gte(startDate).lt(endDate) ;
        try {
            List ticketPriceVos = elasticSearchClient.queryDataFromEsWithoutPaging(queryBuilder,dateFilter,indexName,type,TicketPriceVo.class);
            if(ticketPriceVos.size() > 0 ){
                for(Object obj : ticketPriceVos){
                    TicketPriceVo vo = (TicketPriceVo) obj ;
                    TicketPriceVo ticketPriceVo = ticketLowestPrice.get(vo.getShopId());
                    if(ticketPriceVo == null){
                        ticketLowestPrice.put(vo.getShopId(),vo);
                    }else{
                        if(ticketPriceVo.getPrice() > vo.getPrice()){
                            ticketLowestPrice.put(vo.getShopId(),vo);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(ShopTravelListVo vo : travelList){
            TicketPriceVo ticketPriceVo =  ticketLowestPrice.get(vo.getShopId()) ;
            if(ticketPriceVo != null){
                vo.setTravelTicketLowestPrice(ticketPriceVo.getPrice());
                vo.setTicketCheapestDate(ticketPriceVo.getPriceDate());
            }
        }
    }

    @Override
    public PageResult getShopTravelListTicketPriceLowFirstByResort(String indexName, String type, long resortId, long startDate, long endDate, List<SortField> sortFields, int pageSize, int page,Class clazz,Integer landmarkId,String shopName) {

        Map<Long,ShopTravelListVo> shopTravelListVoHashMap = new HashMap<Long,ShopTravelListVo>();
        List<Long> shopIds = new ArrayList<Long>() ;
        PageResult pageResult = new PageResult();
        pageResult.setPageNum(page);
        pageResult.setPageSize(pageSize);

        int from = (page-1)*pageSize ;

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        QueryBuilder queryBuilder1 = null ;
        QueryBuilder shopNameQueryBuilder = null ;
        if(landmarkId != null){
            //landmarkId要求唯一
            queryBuilder1 = QueryBuilders.termQuery("landmarkId",landmarkId) ;
        }else{
            queryBuilder1 = QueryBuilders.termQuery("resortId",resortId) ;
        }
        if(shopName != null){
            shopNameQueryBuilder = QueryBuilders.matchQuery("shopName",shopName) ;
            boolQueryBuilder.must(shopNameQueryBuilder) ;
        }

        QueryBuilder queryBuilder2 = QueryBuilders.termQuery("priceDateLongValue",startDate) ;
        boolQueryBuilder.must(queryBuilder1).must(queryBuilder2) ;
        try {
            //根据resortId和预定日期的第一天 去按照价格低价的顺序去获取店家id的列表
            elasticSearchClient.queryDataFromEsWithPostFilter(boolQueryBuilder,null ,sortFields,null ,indexName,type,from,pageSize,clazz,pageResult);
            List ticketPriceVos = (List) pageResult.getResult() ;

            for(Object ticketPriceVo : ticketPriceVos){
                TicketPriceVo tpo = (TicketPriceVo) ticketPriceVo ;
                shopIds.add(tpo.getShopId()) ;
            }


            if(ticketPriceVos.size() > 0){
                //根据上一步查询出来的结果 的每个酒店的id 然然后获去酒店的详细信息
                QueryBuilder idsQuery  = QueryBuilders.termsQuery("shopId",shopIds);
                elasticSearchClient.queryDataFromEsWithPostFilter(idsQuery,null ,null,null ,"shop_travel","travel",0,shopIds.size(),ShopTravelListVo.class,pageResult);
            }

            List shopListVos = (List) pageResult.getResult() ;
            for(Object obj : shopListVos){
                ShopTravelListVo stlo = (ShopTravelListVo) obj ;
                shopTravelListVoHashMap.put(stlo.getShopId(),stlo) ;
            }

            List<ShopTravelListVo> shopTravelListVos = new ArrayList<ShopTravelListVo>() ;

            for(Object hotelPriceVo : ticketPriceVos){
                TicketPriceVo tpo = (TicketPriceVo) hotelPriceVo ;
                ShopTravelListVo shopTravelListVo = shopTravelListVoHashMap.get(tpo.getShopId());
                shopTravelListVo.setTicketCheapestDate(tpo.getPriceDate());
                shopTravelListVo.setTravelTicketLowestPrice(tpo.getPrice());
                shopTravelListVos.add(shopTravelListVo) ;
            }
            pageResult.setResult(shopTravelListVos);


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
