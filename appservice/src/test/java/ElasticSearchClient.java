import com.sun.org.apache.xml.internal.dtm.ref.DTMNamedNodeMap;
import com.wanjia.utils.JsonUtil;
import com.wanjia.vo.*;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by blake on 2016/6/21.
 */
public class ElasticSearchClient {

    Settings settings = Settings.settingsBuilder().put("cluster.name","wanjia").put("client.transport.sniff",true).build() ;

    Client client ;

    @Before
    public void initESClient() {
        try {
            client = TransportClient.builder().settings(settings).build().addTransportAddress(
                    new InetSocketTransportAddress(new InetSocketAddress("112.124.15.101",9300)))
                    .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("112.124.49.117",9300)))
                    .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress("112.124.39.68",9300))) ;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void addShopHotelListDataToEs(){

        Random random  = new Random() ;
        List<ShopHotelListVo> vos = new ArrayList<ShopHotelListVo>();
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        for(int i = 1 ; i <= 20 ; i ++){
            ShopHotelListVo vo = new ShopHotelListVo() ;
            vo.setResortId(1);
            vo.setShopId(i);
            vo.setShopName("店家-"+i);
            vo.setRoomId(i);
            vo.setAddressProvince("安徽");
            vo.setAddressCity("池州");
            vo.setAddressDistinct("九华山");
            vo.setAddressDetail("街道"+i);
            if(i % 5 == 0){
                vo.setAdShop(1);
            }else{
                vo.setAdShop(0);
            }

            vo.setDefaultSort(random.nextInt(20));
            int goodNum =  random.nextInt(2000) ;
            vo.setGoodCommentNum(goodNum);
            vo.setRecommendNum(random.nextInt(3000));
            vo.setTotalCommentNum(goodNum+random.nextInt(1000));
            vo.setLandmarkId(i%4);
            ShopListBaseVo.Location location = vo.new Location();

            double lon = random.nextInt(100) ;
            double lat = random.nextInt(90);
            location.setLon(lon);
            location.setLat(lat);
            vo.setLocation(location);
            vo.setShopPic("http://www.whateverblake.com/shoplist_"+i+".jpg");

            bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_hotel").setType("hotel").setId(String.valueOf(i)).setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;

            vos.add(vo);
        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }
    }

    @Test
    public void addShopHotelPriceToEs(){

        Random random  = new Random() ;
        List<HotelPriceVo> vos = new ArrayList<HotelPriceVo>();
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        int id = 0  ;

        for(int i = 1 ; i <= 20 ; i ++){
            DateTime dateTime = new DateTime() ;
            for(int j =1 ; j <= 90 ; j++){
                dateTime = dateTime.plusDays(1) ;
                String dateTime1Str = dateTime.toString("yyyy-MM-dd");
                long timevalue = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(dateTime1Str).getMillis() ;
                HotelPriceVo vo = new HotelPriceVo();
                vo.setRoomId(i);
                vo.setShopId(i);
                vo.setLandmarkId(i%4);
                vo.setShopName("店家-"+i);
                vo.setPriceDate(dateTime1Str);
                vo.setPriceDateLongValue(timevalue);
                vo.setResortId(1);
                vo.setRoomPrice(random.nextInt(500));
                bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_hotel_price").setType("price").setId(String.valueOf(id++)).setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
                vos.add(vo);
            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }
    }


    @Test
    public void addShopRestaurantListDataToEs(){

        Random random  = new Random() ;
        List<ShopRestaurantListVo> vos = new ArrayList<ShopRestaurantListVo>();
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        for(int i = 1 ; i <= 20 ; i ++){
            ShopRestaurantListVo vo = new ShopRestaurantListVo() ;
            vo.setResortId(1);
            vo.setShopId(i);
            vo.setShopName("店家-"+i);
            vo.setAddressProvince("安徽");
            vo.setAddressCity("池州");
            vo.setAddressDistinct("九华山");
            vo.setAddressDetail("街道"+i);
            if(i % 5 == 0){
                vo.setAdShop(1);
            }else{
                vo.setAdShop(0);
            }

            vo.setDefaultSort(random.nextInt(20));
            int goodNum =  random.nextInt(2000) ;
            vo.setGoodCommentNum(goodNum);
            vo.setRecommendNum(random.nextInt(3000));
            vo.setTotalCommentNum(goodNum+random.nextInt(1000));
            vo.setLandmarkId(i%4);
            ShopListBaseVo.Location location = vo.new Location();

            double lon = random.nextInt(100) ;
            double lat = random.nextInt(90);
            location.setLon(lon);
            location.setLat(lat);
            vo.setLocation(location);
            vo.setShopPic("http://www.whateverblake.com/shoplist_"+i+".jpg");
            vo.setFoodTotalNum(random.nextInt(50));
            vo.setFoodAveragePrice(random.nextInt(200));

            bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_restaurant").setType("restaurant").setId(String.valueOf(i)).setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;

            vos.add(vo);
        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }
    }


    @Test
    public void addShopSpecialtyListDataToEs(){

        Random random  = new Random() ;
        List<ShopSpecialtyListVo> vos = new ArrayList<ShopSpecialtyListVo>();
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        for(int i = 1 ; i <= 20 ; i ++){
            ShopSpecialtyListVo vo = new ShopSpecialtyListVo() ;
            vo.setResortId(1);
            vo.setShopId(i);
            vo.setShopName("店家-"+i);
            vo.setAddressProvince("安徽");
            vo.setAddressCity("池州");
            vo.setAddressDistinct("九华山");
            vo.setAddressDetail("街道"+i);
            if(i % 5 == 0){
                vo.setAdShop(1);
            }else{
                vo.setAdShop(0);
            }

            vo.setDefaultSort(random.nextInt(20));
            int goodNum =  random.nextInt(2000) ;
            vo.setGoodCommentNum(goodNum);
            vo.setRecommendNum(random.nextInt(3000));
            vo.setTotalCommentNum(goodNum+random.nextInt(1000));
            vo.setLandmarkId(i%4);
            ShopListBaseVo.Location location = vo.new Location();

            double lon = random.nextInt(100) ;
            double lat = random.nextInt(90);
            location.setLon(lon);
            location.setLat(lat);
            vo.setLocation(location);
            vo.setShopPic("http://www.whateverblake.com/shoplist_"+i+".jpg");
            vo.setSpecialtyLowestPrice(random.nextInt(800));
            bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_specialty").setType("specialty").setId(String.valueOf(i)).setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;

            vos.add(vo);
        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }
    }



    @Test
    public void addShopTravelListDataToEs(){

        Random random  = new Random() ;
        List<ShopTravelListVo> vos = new ArrayList<ShopTravelListVo>();
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        for(int i = 1 ; i <= 20 ; i ++){
            ShopTravelListVo vo = new ShopTravelListVo();

            vo.setResortId(1);
            vo.setShopId(i);
            vo.setShopName("店家-"+i);
            vo.setAddressProvince("安徽");
            vo.setAddressCity("池州");
            vo.setAddressDistinct("九华山");
            vo.setAddressDetail("街道"+i);
            if(i % 5 == 0){
                vo.setAdShop(1);
            }else{
                vo.setAdShop(0);
            }



            vo.setDefaultSort(random.nextInt(20));
            int goodNum =  random.nextInt(2000) ;
            vo.setGoodCommentNum(goodNum);
            vo.setRecommendNum(random.nextInt(3000));
            vo.setTotalCommentNum(goodNum+random.nextInt(1000));
            vo.setLandmarkId(i%4);
            ShopListBaseVo.Location location = vo.new Location();

            double lon = random.nextInt(100) ;
            double lat = random.nextInt(90);
            location.setLon(lon);
            location.setLat(lat);
            vo.setLocation(location);
            vo.setShopPic("http://www.whateverblake.com/shoplist_"+i+".jpg");
            //vo.setTravelTicketLowestPrice(random.nextInt(300));
            vo.setTravelTicketState(1);

            if(i % 3 == 0){
                vo.setTravelGuideLowestPrice(random.nextInt(200));
                vo.setTravelGuideState(1);
            }else if(i % 3 ==1){
                vo.setTravelSpecialState(1);
                vo.setTravelSpecialLowestPrice(random.nextInt(400));
            }

            bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_travel").setType("travel").setId(String.valueOf(i)).setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;

            vos.add(vo);
        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }
    }

    @Test
    public void addShopTravelTicketPriceToEs(){

        Random random  = new Random() ;
        List<TicketPriceVo> vos = new ArrayList<TicketPriceVo>();
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        int id = 0  ;

        for(int i = 1 ; i <= 20 ; i ++){
            DateTime dateTime = new DateTime() ;
            for(int j =1 ; j <= 90 ; j++){
                dateTime = dateTime.plusDays(1) ;
                String dateTime1Str = dateTime.toString("yyyy-MM-dd");
                long timevalue = DateTimeFormat.forPattern("yyyy-MM-dd").parseDateTime(dateTime1Str).getMillis() ;
                TicketPriceVo vo = new TicketPriceVo();
                vo.setTicketId(i);
                vo.setPriceDate(dateTime1Str);
                vo.setPriceDateLongValue(timevalue);
                vo.setResortId(1);
                vo.setShopId(i);
                vo.setLandmarkId(i%4);
                vo.setShopName("店家-"+i);
                vo.setPrice(random.nextInt(500));
                bulkRequestBuilder.add(client.prepareIndex().setIndex("shop_travel_ticketprice").setType("price").setId(String.valueOf(id++)).setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;
                vos.add(vo);
            }

        }

        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }
    }



    @Test
    public void addLandmarkDataToEs(){
        Character[] name = {'A','B','C','D','E','F','G','H','I'} ;

        Random random  = new Random() ;
        List<ResortLandmarkVo> vos = new ArrayList<ResortLandmarkVo>();
        DecimalFormat dcmFmt = new DecimalFormat("0.00");
        BulkRequestBuilder bulkRequestBuilder =  client.prepareBulk() ;

        for(int i = 1 ; i < 9 ; i ++){
            ResortLandmarkVo vo = new ResortLandmarkVo() ;
            vo.setResortId(i % 3);
            vo.setLandmarkName("地标_"+name[i]);
            vo.setLandmarkId(i);
            vo.setLat(random.nextInt(180));
            vo.setLon(random.nextInt(90));
            vo.setIsValid(1);
            vos.add(vo);
            bulkRequestBuilder.add(client.prepareIndex().setIndex("resort_landmark").setType("landmark").setId(String.valueOf(i)).setSource(JsonUtil.toJsonString(vo)).setOpType(IndexRequest.OpType.INDEX)) ;

        }


        BulkResponse bulkResponse =  bulkRequestBuilder.execute().actionGet() ;
        if(bulkResponse.hasFailures()){
            BulkItemResponse [] items =  bulkResponse.getItems() ;
            for(BulkItemResponse item : items){
                if(item.isFailed()){
                    item.getFailure().getCause().printStackTrace() ;
                }
            }
        }
    }


    @Test
    public void deleteByQuery(){
    }

    @Test
    public void testQuery(){

        QueryBuilder queryBuilder =  QueryBuilders.termQuery("resortId",1);
        String sortField = "f_goodCommentNum";
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch("shoplist").setTypes("shopinfo").setSearchType(SearchType.QUERY_THEN_FETCH)
                .setQuery(queryBuilder).setFrom(1).setSize(20).addSort(sortField, SortOrder.DESC) ;
        SearchResponse searchResponse =  searchRequestBuilder.execute().actionGet() ;

        SearchHits searchHits = searchResponse.getHits();
        long totalHits = searchHits.totalHits() ;

        if(totalHits >0){
            SearchHit[] hits = searchHits.getHits() ;
            for (SearchHit hit :hits){
               // System.out.println(hit.getScore());
               System.out.println(hit.getSourceAsString()) ;
            }
        }


    }




}
